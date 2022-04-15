package com.bytezone.wizardry.origin;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public class Messages
// -----------------------------------------------------------------------------------//
{
  private int codeOffset = 185;
  private List<MessageLine> messageLines = new ArrayList<> ();
  private List<Message> messages = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public Messages (byte[] buffer, int scenarioId)
  // ---------------------------------------------------------------------------------//
  {
    int offset = 0;

    while (offset < buffer.length)
    {
      for (int i = 0; i < 504; i += 42)
      {
        String line = scenarioId == 1 ? Utility.getPascalString (buffer, offset + i) //
            : getCodedLine (buffer, offset + i);
        messageLines.add (new MessageLine (line, buffer[offset + i + 40] == 1));
      }
      offset += 512;
    }

    // create messages
    int ptr = 0;
    Message message = null;

    for (MessageLine messageLine : messageLines)
    {
      if (message == null)
        message = new Message (ptr);
      message.addLine (messageLine);
      if (messageLine.endOfMessage)
      {
        messages.add (message);
        message = null;
      }
      ++ptr;
    }
  }

  // ---------------------------------------------------------------------------------//
  private String getCodedLine (byte[] buffer, int offset)
  // ---------------------------------------------------------------------------------//
  {
    int length = buffer[offset++] & 0xFF;
    byte[] translation = new byte[length];
    codeOffset--;

    for (int j = 0; j < length; j++)
    {
      int letter = buffer[offset++] & 0xFF;
      translation[j] = (byte) (letter - (codeOffset - j * 3));
    }

    return new String (translation, 0, length);
  }

  // ---------------------------------------------------------------------------------//
  public List<Message> getMessages ()
  // ---------------------------------------------------------------------------------//
  {
    return messages;
  }

  // ---------------------------------------------------------------------------------//
  public Message getMessage (int id)
  // ---------------------------------------------------------------------------------//
  {
    for (Message message : messages)
      if (message.match (id))
        return message;

    return null;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    for (Message message : messages)
    {
      text.append (message);
      text.append ("\n\n");
    }

    return text.toString ();
  }
}
