package com.bytezone.wizardry.origin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bytezone.wizardry.disk.WizardryDisk;

// -----------------------------------------------------------------------------------//
public class WizardryOrigin
// -----------------------------------------------------------------------------------//
{
  static final int MAZE_AREA = 1;
  static final int MONSTER_AREA = 2;
  static final int TREASURE_TABLE_AREA = 3;
  static final int ITEM_AREA = 4;
  static final int CHARACTER_AREA = 5;
  static final int IMAGE_AREA = 6;
  static final int EXPERIENCE_AREA = 7;

  Messages messages;

  List<Monster> monsters;
  List<Item> items;
  private List<MazeLevel> mazeLevels;

  public enum Square
  {
    NORMAL, STAIRS, PIT, CHUTE, SPINNER, DARK, TRANSFER, OUCHY, BUTTONZ, ROCKWATE, FIZZLE, SCNMSG,
    ENCOUNTE
  }

  public enum Direction
  {
    NORTH, SOUTH, EAST, WEST
  }

  public enum ObjectType
  {
    WEAPON, ARMOR, SHIELD, HELMET, GAUNTLET, SPECIAL, MISC
  }

  public enum Race
  {
    NORACE, HUMAN, ELF, DWARF, GNOME, HOBBIT
  }

  public enum CharacterClass
  {
    FIGHTER, MAGE, PRIEST, THIEF, BISHOP, SAMURAI, LORD, NINJA
  }

  public enum Alignment
  {
    UNALIGN, GOOD, NEUTRAL, EVIL
  }

  public enum CharacterStatus
  {
    OK, AFRAID, ASLEEP, PLYZE, STONED, DEAD, ASHES, LOST
  }

  public enum Attribute
  {
    STRENGTH, IQ, PIETY, VITALITY, AGILITY, LUCK
  }

  // ---------------------------------------------------------------------------------//
  public WizardryOrigin (String diskFileName)
  // ---------------------------------------------------------------------------------//
  {
    File file = new File (diskFileName);
    if (!file.exists ())
    {
      System.out.println ("File does not exist");
      return;
    }

    WizardryDisk disk = new WizardryDisk (diskFileName);
    if (disk == null)
    {
      System.out.println ("Not a Wizardry disk");
      return;
    }

    byte[] buffer = disk.getScenarioData ();
    Header header = new Header (buffer);

    ScenarioData sd = header.get (MAZE_AREA);
    mazeLevels = new ArrayList<> (sd.totalUnits);

    int id = 0;
    for (DataBlock dataBlock : sd.dataBlocks)
      mazeLevels.add (new MazeLevel (++id, dataBlock));

    sd = header.get (MONSTER_AREA);
    monsters = new ArrayList<> (sd.totalUnits);

    id = 0;
    for (DataBlock dataBlock : sd.dataBlocks)
      monsters.add (new Monster (id++, dataBlock));

    sd = header.get (ITEM_AREA);
    items = new ArrayList<> (sd.totalUnits);

    id = 0;
    for (DataBlock dataBlock : sd.dataBlocks)
      items.add (new Item (id++, dataBlock));

    messages = new Messages (disk.getScenarioMessages ());

    if (false)
      for (Square square : Square.values ())
      {
        showExtra (square);
        System.out.println ();
      }

    //    for (Monster monster : monsters)
    //      System.out.println (monster);

    //    for (Item item : items)
    //      System.out.println (item);
  }

  // ---------------------------------------------------------------------------------//
  public List<MazeLevel> getMazeLevels ()
  // ---------------------------------------------------------------------------------//
  {
    return mazeLevels;
  }

  // ---------------------------------------------------------------------------------//
  public List<Monster> getMonsters ()
  // ---------------------------------------------------------------------------------//
  {
    return monsters;
  }

  // ---------------------------------------------------------------------------------//
  public Monster getMonster (int id)
  // ---------------------------------------------------------------------------------//
  {
    return monsters.get (id);
  }

  // ---------------------------------------------------------------------------------//
  public List<Item> getItems ()
  // ---------------------------------------------------------------------------------//
  {
    return items;
  }

  // ---------------------------------------------------------------------------------//
  public Item getItem (int id)
  // ---------------------------------------------------------------------------------//
  {
    return items.get (id);
  }

  // ---------------------------------------------------------------------------------//
  public Message getMessage (int id)
  // ---------------------------------------------------------------------------------//
  {
    return messages.getMessage (id);
  }

  // ---------------------------------------------------------------------------------//
  public Messages getMessages ()
  // ---------------------------------------------------------------------------------//
  {
    return messages;
  }

  // ---------------------------------------------------------------------------------//
  void showExtra (Square square)
  // ---------------------------------------------------------------------------------//
  {
    for (MazeLevel level : mazeLevels)
      for (int col = 0; col < 20; col++)
        for (int row = 0; row < 20; row++)
        {
          MazeCell mazeCell = level.getMazeCell (col, row);
          Extra extra = mazeCell.getExtra ();
          if (extra != null && extra.is (square))
            System.out.printf ("%s  %s%n", extra, mazeCell.getLocation ());
        }
  }
}
