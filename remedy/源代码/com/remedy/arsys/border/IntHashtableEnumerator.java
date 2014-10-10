package com.remedy.arsys.border;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class IntHashtableEnumerator
  implements Enumeration<Object>
{
  IntHashtableEntry entry;
  IntHashtableEntry[] table;
  boolean keys;
  int index;

  IntHashtableEnumerator(IntHashtableEntry[] paramArrayOfIntHashtableEntry, boolean paramBoolean)
  {
    this.table = paramArrayOfIntHashtableEntry;
    this.keys = paramBoolean;
    this.index = paramArrayOfIntHashtableEntry.length;
  }

  public boolean hasMoreElements()
  {
    if (this.entry != null)
      return true;
    while (this.index-- > 0)
      if ((this.entry = this.table[this.index]) != null)
        return true;
    return false;
  }

  public Object nextElement()
  {
    while ((this.entry == null) && (this.index-- > 0) && ((this.entry = this.table[this.index]) == null));
    if (this.entry != null)
    {
      IntHashtableEntry localIntHashtableEntry = this.entry;
      this.entry = localIntHashtableEntry.next;
      return this.keys ? new Integer(localIntHashtableEntry.key) : localIntHashtableEntry.value;
    }
    throw new NoSuchElementException("IntHashtableEnumerator");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.IntHashtableEnumerator
 * JD-Core Version:    0.6.1
 */