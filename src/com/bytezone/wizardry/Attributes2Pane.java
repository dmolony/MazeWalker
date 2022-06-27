package com.bytezone.wizardry;

import com.bytezone.wizardry.data.Character;
import com.bytezone.wizardry.data.WizardryData;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

// -----------------------------------------------------------------------------------//
public class Attributes2Pane extends DataPane
// -----------------------------------------------------------------------------------//
{
  private static final int ID = 0;
  private static final int MAXLEVAC = 1;
  private static final int CHAR_LEV = 2;
  private static final int HP_LEFT = 3;
  private static final int HP_MAX = 4;
  private static final int HP_CALC_MD = 5;
  private static final int AC = 6;
  private static final int REGEN = 7;
  private static final int SWING = 8;

  private TextField[] textOut;

  private WizardryData wizardry;

  // ---------------------------------------------------------------------------------//
  public Attributes2Pane ()
  // ---------------------------------------------------------------------------------//
  {
    super (9, 2);

    setColumnConstraints (90, 50);
    setAllRowConstraints (getRows (), getRowHeight ());      // make all rows the same height

    String[] labelText =
        { "ID", "Max lev AC", "Level", "HP left", "Max HP", "HP calc", "AC", "Regen", "Swing" };
    assert getRows () == labelText.length;

    createLabelsVertical (new LabelPlacement (labelText, 0, 0, HPos.RIGHT, 1));
    textOut = createTextFields (new DataLayout (1, 0, getRows (), Pos.CENTER_RIGHT));
  }

  // ---------------------------------------------------------------------------------//
  void setWizardry (WizardryData wizardry)
  // ---------------------------------------------------------------------------------//
  {
    this.wizardry = wizardry;

    reset (textOut);
  }

  // ---------------------------------------------------------------------------------//
  void update (Character character)
  // ---------------------------------------------------------------------------------//
  {
    setText (textOut[ID], character.id);
    setText (textOut[MAXLEVAC], character.maxlevac);
    setText (textOut[CHAR_LEV], character.charlev);
    setText (textOut[HP_LEFT], character.hpLeft);
    setText (textOut[HP_MAX], character.hpMax);
    setText (textOut[HP_CALC_MD], character.hpCalCmd);
    setText (textOut[AC], character.armourClass);
    setText (textOut[REGEN], character.healPts);
    setText (textOut[SWING], character.swingCount);
  }
}
