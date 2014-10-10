package com.remedy.arsys.support;

public class Holder<T>
{
  private T item;

  public Holder(T paramT)
  {
    set(paramT);
  }

  public T get()
  {
    return this.item;
  }

  public void set(T paramT)
  {
    this.item = paramT;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.Holder
 * JD-Core Version:    0.6.1
 */