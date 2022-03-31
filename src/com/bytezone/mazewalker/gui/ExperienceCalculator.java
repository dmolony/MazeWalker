package com.bytezone.mazewalker.gui;

import com.bytezone.wizardry.origin.Monster;
import com.bytezone.wizardry.origin.WizardryOrigin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

// -----------------------------------------------------------------------------------//
public class ExperienceCalculator extends Pane
// -----------------------------------------------------------------------------------//
{
  private static final int HP_DICE = 0;
  private static final int HP_SIDES = 1;
  private static final int BREATHE = 2;
  private static final int AC = 3;
  private static final int RECSN = 4;
  private static final int MAGE_LEVEL = 5;
  private static final int PRIEST_LEVEL = 6;
  private static final int DRAIN = 7;
  private static final int HEAL = 8;
  private static final int RESIST1 = 9;
  private static final int RESIST2 = 10;
  private static final int ABILITY = 11;
  private static final int TOTAL = 12;

  String[] labelText =
      { "HP # dice", "HP # sides", "Breathe", "Armour class", "Damage # dice", "Mage level",
          "Priest level", "Level drain", "Heal", "Resist 1", "Resist 2", "Abilities", "Total" };

  Label[] labels = new Label[labelText.length];
  TextField[] textIn = new TextField[labelText.length];
  TextField[] textOut = new TextField[labelText.length];
  ComboBox<Monster> monsters = new ComboBox<> ();

  GridPane gridPane = new GridPane ();
  WizardryOrigin wizardry;

  // ---------------------------------------------------------------------------------//
  public ExperienceCalculator (WizardryOrigin wizardry)
  // ---------------------------------------------------------------------------------//
  {
    this.wizardry = wizardry;

    build ();

    gridPane.getColumnConstraints ().add (new ColumnConstraints (135));
    gridPane.getColumnConstraints ().add (new ColumnConstraints (60));
    gridPane.getColumnConstraints ().add (new ColumnConstraints (80));

    gridPane.setHgap (12);
    gridPane.setVgap (8);
    gridPane.setPadding (new Insets (15, 10, 12, 10));      // trbl

    getChildren ().add (new BorderPane (gridPane));
  }

  // ---------------------------------------------------------------------------------//
  private void keyTyped (KeyEvent evt)
  // ---------------------------------------------------------------------------------//
  {
    if (evt.isShortcutDown () || evt.isControlDown () || evt.isMetaDown ())
      return;

    getExperience ();
  }

  // ---------------------------------------------------------------------------------//
  private void getExperience ()
  // ---------------------------------------------------------------------------------//
  {
    if (wizardry.getScenarioId () > 1)
    {
      return;
    }

    int[] values = new int[labels.length];
    for (int i = 0; i < labels.length; i++)
    {
      try
      {
        boolean negative = false;
        String text = textIn[i].getText ();
        if (text == null || text.isBlank ())
          text = "0";

        if (text.equals ("-"))
          continue;

        if (text.startsWith ("-"))
        {
          text = text.substring (1);
          negative = true;
        }

        values[i] = Integer.parseInt (text);
        if (negative)
          values[i] *= -1;
      }
      catch (NumberFormatException e)
      {
        System.out.printf ("rejected: [%s]%n", textIn[i].getText ());
        textIn[i].clear ();
      }
    }

    int expHitPoints = values[HP_DICE] * values[HP_SIDES] * (values[BREATHE] == 0 ? 20 : 40);
    int expAc = 40 * (11 - values[AC]);

    int expMage = getBonus (35, values[MAGE_LEVEL]);
    int expPriest = getBonus (35, values[PRIEST_LEVEL]);
    int expDrain = getBonus (200, values[DRAIN]);
    int expHeal = getBonus (90, values[HEAL]);

    int expDamage = values[RECSN] <= 1 ? 0 : getBonus (30, values[RECSN]);
    int expUnaffect = values[RESIST1] == 0 ? 0 : getBonus (40, (values[RESIST1] / 10 + 1));

    int expFlags1 = getBonus (35, Integer.bitCount (values[RESIST2] & 0x7E));    // 6 bits
    int expFlags2 = getBonus (40, Integer.bitCount (values[ABILITY] & 0x7F));    // 7 bits

    int total = expHitPoints + expAc + expMage + expPriest + expDrain + expHeal + expDamage
        + expUnaffect + expFlags1 + expFlags2;

    textOut[HP_SIDES].setText (getText (expHitPoints));
    textOut[AC].setText (getText (expAc));
    textOut[MAGE_LEVEL].setText (getText (expMage));
    textOut[PRIEST_LEVEL].setText (getText (expPriest));
    textOut[DRAIN].setText (getText (expDrain));
    textOut[HEAL].setText (getText (expHeal));
    textOut[RECSN].setText (getText (expDamage));
    textOut[RESIST1].setText (getText (expUnaffect));
    textOut[RESIST2].setText (getText (expFlags1));
    textOut[ABILITY].setText (getText (expFlags2));

    textOut[TOTAL].setText (getText (total));
  }

  // ---------------------------------------------------------------------------------//
  private int getBonus (int base, int multiplier)
  // ---------------------------------------------------------------------------------//
  {
    if (multiplier == 0)
      return 0;

    int total = base;
    while (multiplier > 1)
    {
      int part = total % 10000;   // get the last 4 digits

      multiplier--;
      total += total;             // double the value

      if (part >= 5000)           // mimics the wizardry bug
        total += 10000;           // yay, free points
    }

    return total;
  }

  // ---------------------------------------------------------------------------------//
  private void build ()
  // ---------------------------------------------------------------------------------//
  {
    Label monsterLabel = new Label ("Monster");
    GridPane.setConstraints (monsterLabel, 0, 0);
    GridPane.setConstraints (monsters, 1, 0);
    gridPane.getChildren ().addAll (monsterLabel, monsters);
    monsters.setVisibleRowCount (30);
    GridPane.setColumnSpan (monsters, 2);
    GridPane.setHalignment (monsterLabel, HPos.RIGHT);

    ObservableList<Monster> list = FXCollections.observableArrayList ();
    list.addAll (wizardry.getMonsters ());

    monsters.setItems (list);

    monsters.getSelectionModel ().selectedItemProperty ()
        .addListener ( (options, oldValue, newValue) ->
        {
          Monster monster = newValue;
          if (monster != null)
          {
            textIn[HP_DICE].setText (monster.hitPoints.level + "");
            textIn[HP_SIDES].setText (monster.hitPoints.faces + "");
            textIn[BREATHE].setText (monster.breathe + "");
            textIn[AC].setText (monster.armourClass + "");
            textIn[RECSN].setText (monster.recsn + "");
            textIn[MAGE_LEVEL].setText (monster.mageSpells + "");
            textIn[PRIEST_LEVEL].setText (monster.priestSpells + "");
            textIn[DRAIN].setText (monster.drainAmt + "");
            textIn[HEAL].setText (monster.healPts + "");
            textIn[RESIST1].setText (monster.unaffect + "");
            textIn[RESIST2].setText (monster.flags1 + "");
            textIn[ABILITY].setText (monster.flags2 + "");

            if (wizardry.getScenarioId () > 1)
              textOut[TOTAL].setText (getText (monster.expamt));
            else
              getExperience ();
          }
        });

    for (int i = 0; i < labels.length; i++)
    {
      labels[i] = new Label (labelText[i]);
      textIn[i] = new TextField ();
      textOut[i] = new TextField ();

      GridPane.setConstraints (labels[i], 0, i + 1);
      GridPane.setConstraints (textIn[i], 1, i + 1);
      GridPane.setConstraints (textOut[i], 2, i + 1);

      textOut[i].setEditable (false);
      textOut[i].setFocusTraversable (false);
      textOut[i].setAlignment (Pos.CENTER_RIGHT);
      GridPane.setHalignment (labels[i], HPos.RIGHT);

      gridPane.getChildren ().add (labels[i]);
      if (i < labels.length - 1)
        gridPane.getChildren ().add (textIn[i]);
      if (i != HP_DICE && i != BREATHE)
        gridPane.getChildren ().add (textOut[i]);

      textIn[i].setOnKeyTyped (e -> keyTyped (e));
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getText (int value)
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%,7d", value);
  }
}
