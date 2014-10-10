package com.remedy.arsys.goat;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class OutputNotes
  implements Serializable
{
  private static final long serialVersionUID = -8840026039656013661L;
  private final Set mActiveLinkFieldsMadeVisible = new HashSet();
  private final Set mActiveLinkFieldsMadeProcessReq = new HashSet();
  private final Set mActiveLinkMenusAttached = new HashSet();
  private final Set mActiveLinkGuidesCalled = new HashSet();
  private final Set mFieldsInDOM = new HashSet();
  private final Set mFieldsInJS = new HashSet();
  private final Set mFieldsInRP = new HashSet();
  private final Set mFieldMenus = new HashSet();
  private final Set mParentExecDep = new HashSet();
  private final Set mParentHidden = new HashSet();
  private final Set mGroupFieldMenus = new HashSet();
  private boolean mGroupMapping = false;
  private boolean mPerformActionDynamic = false;
  private boolean mGroupField = false;
  private int mMaxFieldX = 0;
  private int mMaxFieldY = 0;
  private boolean mIndirectChangeFieldVisibility = false;
  private Set<String> mTemplates = Collections.synchronizedSet(new HashSet());

  public void addDimensions(int paramInt1, int paramInt2)
  {
    if (this.mMaxFieldX < paramInt1)
      this.mMaxFieldX = paramInt1;
    if (this.mMaxFieldY < paramInt2)
      this.mMaxFieldY = paramInt2;
  }

  public int getMaxFieldX()
  {
    return this.mMaxFieldX;
  }

  public int getMaxFieldY()
  {
    return this.mMaxFieldY;
  }

  public Set getFieldSetInDOM()
  {
    return Collections.unmodifiableSet(this.mFieldsInDOM);
  }

  public Set getFieldSetInJS()
  {
    return Collections.unmodifiableSet(this.mFieldsInJS);
  }

  public Set getParentExecDep()
  {
    return Collections.unmodifiableSet(this.mParentExecDep);
  }

  public Set getParentHidden()
  {
    return Collections.unmodifiableSet(this.mParentHidden);
  }

  public Set getFieldSetInRunProcess()
  {
    return Collections.unmodifiableSet(this.mFieldsInRP);
  }

  public Set getMenuSetAttached()
  {
    return Collections.unmodifiableSet(this.mActiveLinkMenusAttached);
  }

  public Set getFieldMenuSet()
  {
    return Collections.unmodifiableSet(this.mFieldMenus);
  }

  public Set getGroupFieldMenuSet()
  {
    return Collections.unmodifiableSet(this.mGroupFieldMenus);
  }

  public Set getFieldSetMadeVisible()
  {
    return Collections.unmodifiableSet(this.mActiveLinkFieldsMadeVisible);
  }

  public Set getGuideSetCalled()
  {
    return Collections.unmodifiableSet(this.mActiveLinkGuidesCalled);
  }

  public Set<String> getTemplates()
  {
    return Collections.unmodifiableSet(this.mTemplates);
  }

  public boolean isPerformActionDynamic()
  {
    return this.mPerformActionDynamic;
  }

  public boolean isGroupFieldPresent()
  {
    return this.mGroupField;
  }

  public boolean getIndirectChangeFieldVisibility()
  {
    return this.mIndirectChangeFieldVisibility;
  }

  public void addDOMField(int paramInt)
  {
    this.mFieldsInDOM.add(Integer.valueOf(paramInt));
  }

  public void addJSField(int paramInt)
  {
    this.mFieldsInJS.add(Integer.valueOf(paramInt));
  }

  public void addParentExecDep(int paramInt)
  {
    this.mParentExecDep.add(Integer.valueOf(paramInt));
  }

  public void addParentHidden(int paramInt)
  {
    this.mParentHidden.add(Integer.valueOf(paramInt));
  }

  public void addRunProcessField(int paramInt)
  {
    this.mFieldsInRP.add(Integer.valueOf(paramInt));
  }

  public void addFieldMadeVisible(int paramInt)
  {
    this.mActiveLinkFieldsMadeVisible.add(Integer.valueOf(paramInt));
  }

  public void addMenuAttached(String paramString)
  {
    this.mActiveLinkMenusAttached.add(paramString);
  }

  public void addFieldMenus(String paramString)
  {
    this.mFieldMenus.add(paramString);
  }

  public void addGroupFieldMenus(String paramString)
  {
    assert (this.mGroupField == true);
    this.mGroupFieldMenus.add(paramString);
  }

  public void addGroupMenuMapping(String paramString)
  {
    this.mGroupFieldMenus.add(paramString);
    this.mGroupMapping = true;
  }

  public boolean hasGroupMenuMapping()
  {
    return this.mGroupMapping;
  }

  public void setGroupFieldPresent(boolean paramBoolean)
  {
    this.mGroupField = paramBoolean;
  }

  public void setPerformActionDynamic()
  {
    this.mPerformActionDynamic = true;
  }

  public void addCalledGuide(String paramString)
  {
    this.mActiveLinkGuidesCalled.add(paramString);
  }

  public void addIndirectChangeFieldVisibility()
  {
    this.mIndirectChangeFieldVisibility = true;
  }

  public void addTemplate(String paramString)
  {
    this.mTemplates.add(paramString);
  }

  public void addFieldMadeProcessReq(int paramInt)
  {
    this.mActiveLinkFieldsMadeProcessReq.add(Integer.valueOf(paramInt));
  }

  public Set getFieldSetMadeProcessReq()
  {
    return Collections.unmodifiableSet(this.mActiveLinkFieldsMadeProcessReq);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.OutputNotes
 * JD-Core Version:    0.6.1
 */