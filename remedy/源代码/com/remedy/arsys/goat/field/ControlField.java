/*    */ package com.remedy.arsys.goat.field;
/*    */ 
/*    */ import com.bmc.arsys.api.ARException;
/*    */ import com.bmc.arsys.api.DataType;
/*    */ import com.bmc.arsys.api.DisplayInstanceMap;
/*    */ import com.bmc.arsys.api.DisplayPropertyMap;
/*    */ import com.bmc.arsys.api.Field;
/*    */ import com.bmc.arsys.api.FieldCriteria;
/*    */ import com.bmc.arsys.api.Value;
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*    */ import com.remedy.arsys.goat.Form;
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.GoatImage;
/*    */ import com.remedy.arsys.goat.GoatImage.Fetcher;
/*    */ import com.remedy.arsys.goat.OutputNotes;
/*    */ import com.remedy.arsys.goat.aspects.skins.ControlFieldAspect;
/*    */ import com.remedy.arsys.log.Log;
/*    */ import com.remedy.arsys.stubs.ServerLogin;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ import com.remedy.arsys.support.FontTable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ public class ControlField extends GoatField
/*    */ {
/*    */   private static final long serialVersionUID = -54991444953134387L;
/*    */   public static final int CONTROL_TYPE_MENU = -1;
/*    */   public static final int CONTROL_TYPE_BUTTON = 0;
/*    */   public static final int CONTROL_TYPE_URL = 1;
/*    */   public static final int CONTROL_TYPE_DONTCARE = 2;
/*    */   private boolean mImageFlat;
/*    */   private int mAccess;
/*    */   private int mControlType;
/*    */   private String mButtonText;
/*    */   private String mFont;
/*    */   private String mColour;
/*    */   private String mHtmlColour;
/*    */   private boolean mTransparent;
/*    */   private transient GoatImage mImage;
/*    */   private transient GoatImage mDisabledImage;
/*    */   private Value mImageValue;
/*    */   private Value mDisabledImageValue;
/*    */   private boolean mImageMaintainRatio;
/*    */   private boolean mScaleImageToFit;
/*    */   private int mImagePosition;
/*    */   private int mTextJustify;
/*    */   private String mImgButtonAltText;
/*    */   private int mImgBtnMouseoverEffect;
/*    */ 
/*    */   public ControlField(Form paramForm, Field paramField, int paramInt)
/*    */   {
/*  1 */     super(paramForm, paramField, paramInt); if (getMFont() == null) setMFont(FontTable.mapFontToClassName("Default")); if (getMARBox() == null) setMInView(false); if (getMAccess() == 0) setMAccess(2); if (getMControlType() == 2) setMInView(false); else if (getMControlType() == 1) setMColour(getMHtmlColour());  } 
/*  1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { if (arg0 == 2) { int i = propToInt(arg1); if ((i & 0x1) != 0) setMControlType(0); else if ((i & 0x10) != 0) setMControlType(1); else setMControlType(2);  } else if (arg0 == 110) { setMButtonText(propToString(arg1)); } else if (arg0 == 90) { setMTextJustify(propToInt(arg1)); } else if (arg0 == 145) { setMTransparent(propToBool(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BACKGROUND_MODE: " + isMTransparent()); } else if (arg0 == 111) { setMImageFlat(propToBool(arg1)); } else if (arg0 == 112) { setMImagePosition(propToInt(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_IMAGE_POSITION: " + getMImagePosition()); } else if (arg0 == 114) { setMImageMaintainRatio(propToBool(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_MAINTAIN_RATIO: " + isMImageMaintainRatio()); } else if (arg0 == 113) { setMScaleImageToFit(propToBool(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_SCALE_IMAGE: " + isMScaleImageToFit()); } else if (arg0 == 5) { setMAccess(propToInt(arg1)); } else if (arg0 == 24) { setMColour(propToHTMLColour(arg1)); } else if (arg0 == 84) { setMHtmlColour(propToHTMLColour(arg1)); } else if ((arg0 == 22) || (arg0 == 81)) { setMFont(FontTable.mapFontToClassName(propToString(arg1))); } else if (arg0 == 5066) { setMImgButtonAltText(propToString(arg1)); }
/*    */     else
/*    */     {
/*  1 */       LocalImageFetcher localLocalImageFetcher;
/*  1 */       if (arg0 == 101) { localLocalImageFetcher = new LocalImageFetcher(); setMImageValue(arg1);
/*    */         try { this.mImage = propToGoatImage(this.mImageValue, localLocalImageFetcher, getMLForm().getServerName()); } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) { MLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage()); } } else if (arg0 == 5226) { localLocalImageFetcher = new LocalImageFetcher(); setMDisabledImageValue(arg1);
/*    */         try { this.mDisabledImage = propToGoatImage(this.mDisabledImageValue, localLocalImageFetcher, getMLForm().getServerName()); } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException1) { MLog.log(Level.SEVERE, localBadDisplayPropertyException1.getMessage()); } } else if (arg0 == 334) { setMImgBtnMouseoverEffect(propToInt(arg1)); } else { super.handleProperty(arg0, arg1); }  }  } 
/*  1 */   public final Box[] placeQuadrants() { Box localBox1 = getMARBox().toBox(); ControlField localControlField1 = this; int i = getMImage_aroundBody1$advice(this, localControlField1, ControlFieldAspect.aspectOf(), localControlField1, null).getW(); ControlField localControlField2 = this; int j = getMImage_aroundBody3$advice(this, localControlField2, ControlFieldAspect.aspectOf(), localControlField2, null).getH(); int k = localBox1.mW - 1; int m = localBox1.mH - 1; int n = k / 2; int i1 = m / 2; if (!isMScaleImageToFit()) { if (i < n - 4) n = i + 4; if (j < i1 - 4) i1 = i + 4;
/*    */     }
/*  1 */     Box localBox2;
/*    */     Box localBox4;
/*  1 */     if (getMImagePosition() == 0) { localBox2 = localBox1.wholeChildBox(); if (!isMImageFlat()) { if (localBox2.mW > 0) localBox2.mW -= 1; if (localBox2.mH > 0) localBox2.mH -= 1;  } localBox4 = null; } else if (getMImagePosition() == 1) { localBox2 = new Box(0L, 0L, n, m); localBox4 = new Box(n, 0L, k, m); } else if (getMImagePosition() == 2) { localBox2 = new Box(k - n, 0L, k, m); localBox4 = new Box(0L, 0L, k - n, m); } else if (getMImagePosition() == 3) { localBox2 = new Box(0L, 0L, k, i1); localBox4 = new Box(0L, i1, k, m); } else { localBox2 = new Box(0L, m - i1, k, m); localBox4 = new Box(0L, 0L, k, i1);
/*    */     }
/*  1 */     Box localBox3;
/*  1 */     if (isMScaleImageToFit()) { if (isMImageMaintainRatio()) localBox3 = localBox2.scaleCentredBox(i, j); else localBox3 = localBox2.wholeChildBox();  } else localBox3 = localBox2.newCentredBox(i, j); assert ((localBox3.mW >= 0) && (localBox3.mH >= 0)); Box[] arrayOfBox = new Box[3]; arrayOfBox[0] = localBox2; arrayOfBox[1] = localBox3; arrayOfBox[2] = localBox4; return arrayOfBox; } 
/*  1 */   public void addToOutputNotes(OutputNotes arg0, int arg1) { super.addToOutputNotes(arg0, arg1); } 
/*  1 */   protected void setDefaultDisplayProperties() { super.setDefaultDisplayProperties(); setMAccess(0); setMImgBtnMouseoverEffect(1); } 
/*  1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { arg0.defaultReadObject();
/*    */     try { if (getMImageValue() != null) setMImage(propToGoatImage(getMImageValue(), new LocalImageFetcher(), getMLForm().getServerName())); if (getMDisabledImageValue() != null) setMDisabledImage(propToGoatImage(getMDisabledImageValue(), new LocalImageFetcher(), getMLForm().getServerName()));  } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) {  }  } 
/*  1 */   protected void setMImageFlat(boolean arg0) { this.mImageFlat = arg0; } 
/*  1 */   public boolean isMImageFlat() { return this.mImageFlat; } 
/*  1 */   protected void setMAccess(int arg0) { this.mAccess = arg0; } 
/*  1 */   public int getMAccess() { return this.mAccess; } 
/*  1 */   protected void setMControlType(int arg0) { this.mControlType = arg0; } 
/*  1 */   public int getMControlType() { return this.mControlType; } 
/*  1 */   protected void setMHtmlColour(String arg0) { this.mHtmlColour = arg0; } 
/*  1 */   protected String getMHtmlColour() { return this.mHtmlColour; } 
/*  1 */   protected void setMColour(String arg0) { this.mColour = arg0; } 
/*  1 */   public String getMColour() { return this.mColour; } 
/*  1 */   protected void setMFont(String arg0) { this.mFont = arg0; } 
/*  1 */   public String getMFont() { return this.mFont; } 
/*  1 */   protected void setMButtonText(String arg0) { this.mButtonText = arg0; } 
/*  1 */   public String getMButtonText() { return this.mButtonText; } 
/*  1 */   protected void setMTransparent(boolean arg0) { this.mTransparent = arg0; } 
/*  1 */   public boolean isMTransparent() { return this.mTransparent; } 
/*  1 */   protected void setMImage(GoatImage arg0) { this.mImage = arg0; } 
/*  1 */   public GoatImage getMImage() { return this.mImage; } 
/*  1 */   protected void setMDisabledImage(GoatImage arg0) { this.mDisabledImage = arg0; } 
/*  1 */   public GoatImage getMDisabledImage() { return this.mDisabledImage; } 
/*  1 */   private void setMImageValue(Value arg0) { this.mImageValue = arg0; } 
/*  1 */   private Value getMImageValue() { return this.mImageValue; } 
/*  1 */   private void setMDisabledImageValue(Value arg0) { this.mDisabledImageValue = arg0; } 
/*  1 */   private Value getMDisabledImageValue() { return this.mDisabledImageValue; } 
/*  1 */   protected void setMImageMaintainRatio(boolean arg0) { this.mImageMaintainRatio = arg0; } 
/*  1 */   public boolean isMImageMaintainRatio() { return this.mImageMaintainRatio; } 
/*  1 */   protected void setMScaleImageToFit(boolean arg0) { this.mScaleImageToFit = arg0; } 
/*  1 */   public boolean isMScaleImageToFit() { return this.mScaleImageToFit; } 
/*  1 */   protected void setMImagePosition(int arg0) { this.mImagePosition = arg0; } 
/*  1 */   public int getMImagePosition() { return this.mImagePosition; } 
/*  1 */   protected void setMTextJustify(int arg0) { this.mTextJustify = arg0; } 
/*  1 */   public int getMTextJustify() { return this.mTextJustify; } 
/*  1 */   protected void setMImgButtonAltText(String arg0) { this.mImgButtonAltText = arg0; } 
/*  1 */   public String getMImgButtonAltText() { return this.mImgButtonAltText; } 
/*  1 */   protected void setMImgBtnMouseoverEffect(int arg0) { this.mImgBtnMouseoverEffect = arg0; } 
/*  1 */   public int getMImgBtnMouseoverEffect() { return this.mImgBtnMouseoverEffect; }
/*    */ 
/*    */ 
/*    */   public class LocalImageFetcher
/*    */     implements GoatImage.Fetcher
/*    */   {
/*    */     private static final long serialVersionUID = 5430293912891169327L;
/*    */ 
/*    */     public LocalImageFetcher()
/*    */     {
/*    */     }
/*    */ 
/*    */     public byte[] reFetchImageData()
/*    */     {
/*    */       Field localField;
/*    */       try
/*    */       {
/*    */         FieldCriteria localFieldCriteria = new FieldCriteria();
/*    */         localFieldCriteria.setPropertiesToRetrieve(FieldCriteria.INSTANCE_LIST);
/*    */         localField = SessionData.get().getServerLogin(ControlField.this.getMLForm().getServerName()).getField(ControlField.this.getMLForm().getFormName(), ControlField.this.getMFieldID(), localFieldCriteria);
/*    */       }
/*    */       catch (ARException localARException)
/*    */       {
/*    */         return null;
/*    */       }
/*    */       catch (GoatException localGoatException)
/*    */       {
/*    */         return null;
/*    */       }
/*    */       DisplayInstanceMap localDisplayInstanceMap = localField.getDisplayInstance();
/*    */       Iterator localIterator = localDisplayInstanceMap.entrySet().iterator();
/*    */       while (localIterator.hasNext())
/*    */       {
/*    */         Map.Entry localEntry1 = (Map.Entry)localIterator.next();
/*    */         if (((Integer)localEntry1.getKey()).intValue() == ControlField.this.getMView())
/*    */         {
/*    */           DisplayPropertyMap localDisplayPropertyMap = null;
/*    */           Object localObject1 = localDisplayInstanceMap.entrySet().iterator();
/*    */           while (((Iterator)localObject1).hasNext())
/*    */           {
/*    */             localObject2 = (Map.Entry)((Iterator)localObject1).next();
/*    */             if ((((Integer)((Map.Entry)localObject2).getKey()).intValue() == ControlField.this.getMView()) && (((DisplayPropertyMap)((Map.Entry)localObject2).getValue()).size() > 0))
/*    */             {
/*    */               localDisplayPropertyMap = (DisplayPropertyMap)((Map.Entry)localObject2).getValue();
/*    */               break;
/*    */             }
/*    */           }
/*    */           if (localDisplayPropertyMap == null)
/*    */             return null;
/*    */           localObject1 = null;
/*    */           Object localObject2 = localDisplayPropertyMap.entrySet().iterator();
/*    */           while (((Iterator)localObject2).hasNext())
/*    */           {
/*    */             Map.Entry localEntry2 = (Map.Entry)((Iterator)localObject2).next();
/*    */             if (((Integer)localEntry2.getKey()).intValue() == 101)
/*    */               try
/*    */               {
/*    */                 Value localValue = (Value)localEntry2.getValue();
/*    */                 if (localValue.getDataType().equals(DataType.CHAR))
/*    */                 {
/*    */                   String str = localValue.getValue().toString();
/*    */                   localObject1 = GoatImage.getImageReferenceData(str, ControlField.this.getMLForm().getServerName());
/*    */                 }
/*    */                 else
/*    */                 {
/*    */                   localObject1 = ControlField.propToByteArray(localValue);
/*    */                 }
/*    */                 return localObject1;
/*    */               }
/*    */               catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
/*    */               {
/*    */                 GoatField.MLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage());
/*    */               }
/*    */           }
/*    */         }
/*    */       }
/*    */       return null;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.ControlField
 * JD-Core Version:    0.6.1
 */