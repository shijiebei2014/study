package com.remedy.arsys.border;

class IntHashtableEntry
{
  IntHashtableEntry next;
  Object value;
  int hash;
  int key;

  protected Object clone()
  {
    IntHashtableEntry localIntHashtableEntry = new IntHashtableEntry();
    localIntHashtableEntry.hash = this.hash;
    localIntHashtableEntry.key = this.key;
    localIntHashtableEntry.value = this.value;
    localIntHashtableEntry.next = (this.next != null ? (IntHashtableEntry)this.next.clone() : null);
    return localIntHashtableEntry;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.IntHashtableEntry
 * JD-Core Version:    0.6.1
 */