/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ 
/*   */ public class ViewField extends GoatField
/*   */ {
/*   */   private static final long serialVersionUID = -4679715377188198136L;
/*   */   private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_AUTO = 0;
/*   */   private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_ON = 1;
/*   */   private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_HIDDEN = 2;
/*   */   private static final int AR_DVAL_VIEWFIELD_BORDERS_DEFAULT = 0;
/*   */   private static final int AR_DVAL_VIEWFIELD_BORDERS_NONE = 1;
/*   */   private static final int AR_DVAL_VIEWFIELD_BORDERS_ENABLE = 2;
/*   */   private static final String AR_TEMPLATE_PREFIX = "template:";
/*   */   private boolean mDisableChange;
/*   */   private String mScrollbars;
/*   */   private String mBorder;
/*   */   private String mDefaultValue;
/*   */   private String mTemplateName;
/*   */   private String mBorderColor;
/*   */ 
/*   */   public ViewField(Form paramForm, Field paramField, int paramInt)
/*   */   {
/* 1 */     super(paramForm, paramField, paramInt); setMEmitOptimised(1); if (getMBorder() == null) setMBorder("1"); if (getMScrollbars() == null) setMScrollbars("auto");
/*   */   }
/*   */ 
/*   */   protected void handleProperty(int arg0, Value arg1)
/*   */     throws DisplayPropertyMappers.BadDisplayPropertyException
/*   */   {
/*   */     int i;
/* 1 */     switch (arg0) { case 5024:
/* 1 */       i = propToInt(arg1); switch (i) { case 2:
/* 1 */         setMScrollbars("no"); break;
/*   */       case 1:
/* 1 */         setMScrollbars("yes"); break;
/*   */       case 0:
/*   */       default:
/* 1 */         setMScrollbars("auto"); } break;
/*   */     case 5025:
/* 1 */       i = propToInt(arg1); switch (i) { case 1:
/* 1 */         setMBorder("0"); break;
/*   */       case 2:
/*   */       default:
/* 1 */         setMBorder("1"); } break;
/*   */     case 228:
/* 1 */       setMDisableChange(propToBool(arg1)); break;
/*   */     case 80:
/* 1 */       setMDefaultValue(propToString(arg1)); int j = getMDefaultValue().indexOf("template:"); if (j != -1) setTemplateName(getMDefaultValue().substring(j + "template:".length())); break;
/*   */     case 5210:
/* 1 */       setMViewBorderColor(propToHTMLColour(arg1)); break;
/*   */     default:
/* 1 */       super.handleProperty(arg0, arg1); }  } 
/* 1 */   protected void setMDisableChange(boolean arg0) { this.mDisableChange = arg0; } 
/* 1 */   public boolean isMDisableChange() { return this.mDisableChange; } 
/* 1 */   protected void setMScrollbars(String arg0) { this.mScrollbars = arg0; } 
/* 1 */   public String getMScrollbars() { return this.mScrollbars; } 
/* 1 */   protected void setMBorder(String arg0) { this.mBorder = arg0; } 
/* 1 */   public String getMBorder() { return this.mBorder; } 
/* 1 */   protected void setMDefaultValue(String arg0) { this.mDefaultValue = arg0; } 
/* 1 */   protected void setMViewBorderColor(String arg0) { this.mBorderColor = arg0; } 
/* 1 */   public String getMViewBorderColor() { return this.mBorderColor; } 
/* 1 */   public String getMDefaultValue() { return this.mDefaultValue; } 
/* 1 */   public String getTemplateName() { return this.mTemplateName; } 
/* 1 */   public void setTemplateName(String arg0) { this.mTemplateName = arg0; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.ViewField
 * JD-Core Version:    0.6.1
 */