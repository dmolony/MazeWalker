package com.bytezone.mazewalker.gui;

import com.bytezone.wizardry.origin.Damage;
import com.bytezone.wizardry.origin.Extra;
import com.bytezone.wizardry.origin.Location;
import com.bytezone.wizardry.origin.MazeLevel;
import com.bytezone.wizardry.origin.Message;
import com.bytezone.wizardry.origin.Monster;
import com.bytezone.wizardry.origin.WizardryOrigin;
import com.bytezone.wizardry.origin.WizardryOrigin.Trade;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// -----------------------------------------------------------------------------------//
public class SpecialsPane extends BasePane
// -----------------------------------------------------------------------------------//
{
  ComboBox<MazeLevel> mazeLevelList = new ComboBox<> ();

  TextField[] textOut1;
  TextField[] textOut2;
  TextField[] textOut3;
  TextField[] textOut4;
  TextField[] textOut5;
  TextField[] textOut6;
  TextField[] textOut7;

  // ---------------------------------------------------------------------------------//
  public SpecialsPane (WizardryOrigin wizardry, Stage stage)
  // ---------------------------------------------------------------------------------//
  {
    super (wizardry, stage);

    int width = 65;
    setColumnConstraints (110, 120, width, width, width, width, width, 320);

    LabelPlacement lp0 = new LabelPlacement (0, 0, HPos.RIGHT, 1);
    DataPlacement dp0 = new DataPlacement (1, 0, Pos.CENTER_LEFT, 1);
    createComboBox ("Maze Level", mazeLevelList, wizardry.getMazeLevels (),
        (options, oldValue, newValue) -> update (newValue), lp0, dp0);

    // special squares
    String[] squaresText = new String[16];
    for (int i = 0; i < squaresText.length; i++)
      squaresText[i] = i + "";

    LabelPlacement lp1 = new LabelPlacement (0, 5, HPos.RIGHT, 1);
    DataPlacement dp1 = new DataPlacement (1, 5, Pos.CENTER_LEFT, 1);
    textOut1 = createTextFields (squaresText, lp1, dp1);

    textOut2 = createTextFields (16, new DataPlacement (2, 5, Pos.CENTER_RIGHT, 1));
    textOut3 = createTextFields (16, new DataPlacement (3, 5, Pos.CENTER_RIGHT, 1));
    textOut4 = createTextFields (16, new DataPlacement (4, 5, Pos.CENTER_RIGHT, 1));
    textOut5 = createTextFields (16, new DataPlacement (5, 5, Pos.CENTER_RIGHT, 1));
    textOut6 = createTextFields (16, new DataPlacement (6, 5, Pos.CENTER_RIGHT, 1));
    textOut7 = createTextFields (16, new DataPlacement (7, 5, Pos.CENTER_LEFT, 1));

    // headings
    createLabel ("Square", 1, 4, HPos.LEFT, 1);
    createLabel ("Aux 0", 2, 4, HPos.LEFT, 1);
    createLabel ("Aux 1", 3, 4, HPos.LEFT, 1);
    createLabel ("Aux 2", 4, 4, HPos.LEFT, 1);
    createLabel ("Total", 5, 4, HPos.LEFT, 1);
    createLabel ("Msg #", 6, 4, HPos.LEFT, 1);
    createLabel ("Description", 7, 4, HPos.LEFT, 1);

    mazeLevelList.getSelectionModel ().select (0);
  }

  // ---------------------------------------------------------------------------------//
  private void update (MazeLevel mazeLevel)
  // ---------------------------------------------------------------------------------//
  {
    for (int i = 0; i < 16; i++)
    {
      Extra extra = mazeLevel.getExtra ()[i];

      int aux0 = extra.aux[0];
      int aux1 = extra.aux[1];
      int aux2 = extra.aux[2];

      setText (textOut1[i], extra.square);
      setText (textOut2[i], aux0);
      setText (textOut3[i], aux1);
      setText (textOut4[i], aux2);
      setText (textOut5[i], extra.locations.size ());

      setText (textOut6[i], "");

      StringBuilder description = new StringBuilder ();

      switch (extra.square)
      {
        case SCNMSG:
          setText (textOut6[i], aux1);
          Message message = wizardry.getMessage (aux1);
          if (message.getId () != aux1)
            description.append (String.format ("%d = Invalid message id. ", aux1));

          switch (aux2)
          {
            case 1:                                 //
              description.append ("1 = ??");
              break;

            case 2:                                 // TRYGET               
              // special obtain (only blue ribbon so far)
              description.append ("Obtain : " + wizardry.getItem (aux0));
              break;

            case 3:                                 // WHOWADE
              if (aux0 > 0)
                description.append ("Wade : " + wizardry.getItemName (aux0));
              break;

            case 4:                                 // GETYN
              if (aux0 >= 0)
                description.append ("Encounter : " + wizardry.getMonster (aux0));
              else if (aux0 > -1200)
                description.append ("Obtain : " + wizardry.getItem (aux0 * -1 - 1000));
              else
              {
                Trade trade = wizardry.getItemTrade (aux0);
                description.append ("Trade : " + wizardry.getItemName (trade.item1 ()) + " for "
                    + wizardry.getItemName (trade.item2 ()));
              }
              break;

            case 5:                               // ITM2PASS
              description.append ("Requires : " + wizardry.getItemName (aux0));
              break;

            case 6:                               // CHKALIGN
              description.append ("6 = ??");
              break;

            case 7:                               // CHKAUX0
              description.append ("7 = ??");
              break;

            case 8:                               // BCK2SHOP
              description.append ("8 = Return to castle??");
              break;

            case 9:                               // LOOKOUT
              description.append (String.format ("Look out : surrounded by fights"));
              break;

            case 10:                              // RIDDLES
              description.append ("Answer : " + wizardry.getMessageText (aux0));
              break;

            case 11:                              // FEEIS
              description.append ("Pay : " + wizardry.getMessageText (aux0));
              break;

            case 12:
              description.append ("12 = ??");
              break;

            case 13:
              if (aux0 > 0)
                description.append ("Requires : " + wizardry.getItemName (aux0));
              else
                description.append ("13 = ??");
              break;

            case 14:
              description.append ("14 = ??");
              break;
          }
          break;

        case STAIRS:
          Location location = new Location (extra.aux);
          description.append (String.format ("Stairs to : %s", location));
          break;

        case PIT:
          Damage damage = new Damage (extra.aux);
          description.append (String.format ("Pit - %s", damage));
          break;

        case CHUTE:
          location = new Location (extra.aux);
          description.append (String.format ("Chute to : %s", location));
          break;

        case SPINNER:
          description.append ("Spinner");
          break;

        case DARK:
          break;

        case TRANSFER:
          location = new Location (extra.aux);
          description.append (String.format ("Teleport to : %s", location));
          break;

        case OUCHY:
          break;

        case BUTTONZ:
          description.append (String.format ("Elevator levels : %d to %d", aux2, aux1));
          break;

        case ROCKWATE:
          description.append ("Rock/water");
          break;

        case FIZZLE:
          description.append ("Spells cannot be cast here");
          break;

        case ENCOUNTE:
          Monster monster = wizardry.getMonster (aux2);
          String when = aux0 == -1 ? "always" : aux0 + " left";
          description.append (String.format ("%s (%s)", monster, when));
          //          if (!lair)
          //            description.append ("\n\nError - room is not a LAIR");
          break;

        case NORMAL:
          break;
      }
      setText (textOut7[i], description.toString ());
    }
  }
}
