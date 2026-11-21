package com.caissemagasin.app;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
 final int navy=0xff081b36, blue=0xff14558b, gold=0xffffd56a, green=0xff13833d;
 LinearLayout root,body,caisseCategories; TextView caisseStock; Spinner caisseUnit; EditText qty,price,codeField,quickDiscount; Spinner discountTarget; LinkedHashMap<Integer,Double> pendingRemises=new LinkedHashMap<>(); double pendingDiscount=0; int pendingTarget=1; TextView caisseSelectedName,caisseSelectedQty; boolean remiseValidee=false; Spinner quickCategory,quickProduct,quickCount; String chosen="",chosenCode="";
 ArrayList<JSONObject> products=new ArrayList<>(),cart=new ArrayList<>(),sales=new ArrayList<>();
 String[] cats={"Viandes","Poissons","Fruits & Légumes","Aliments & Épicerie","Produits laitiers & Fromages","Boissons","Boulangerie & Viennoiserie","Produits d'entretien","Bricolage","Électricité","Autres produits","Tous les produits"};
 String[] catIcons={"🥩","🐟","🥦","🛒","🧀","🥤","🥐","🧴","🛠","💡","▦","🛍"};
 int[] catColors={0xffad1723,0xff1669a5,0xff258535,0xffdb8a00,0xffd19b16,0xffd43327,0xffa85718,0xff168e9b,0xffbd741e,0xff244d9b,0xff304f86,0xffdf9a06};
 String productIcon(String name){String n=name.toLowerCase(Locale.ROOT);if(n.contains("bœuf")||n.contains("steak")||n.contains("entrecôte"))return "🥩";if(n.contains("poulet")||n.contains("dinde"))return "🍗";if(n.contains("agneau")||n.contains("merguez")||n.contains("chipolata"))return "🥓";if(n.contains("saumon")||n.contains("thon")||n.contains("cabillaud")||n.contains("sardine")||n.contains("dorade"))return "🐟";if(n.contains("crevette"))return "🦐";if(n.contains("pomme")||n.contains("orange")||n.contains("banane")||n.contains("fraise"))return "🍎";if(n.contains("tomate")||n.contains("carotte")||n.contains("salade"))return "🥬";if(n.contains("lait")||n.contains("yaourt"))return "🥛";if(n.contains("fromage")||n.contains("emmental")||n.contains("camembert"))return "🧀";if(n.contains("baguette")||n.contains("pain"))return "🥖";if(n.contains("croissant"))return "🥐";if(n.contains("eau")||n.contains("jus")||n.contains("soda"))return "🥤";if(n.contains("lessive")||n.contains("nettoyant"))return "🧴";if(n.contains("ampoule"))return "💡";return "🛒";}
 String[] units={"pièce","kg","g","L","mL","bouteille","canette","boîte","carton","sachet","pack","pack de 6","pack de 8","paquet","barquette","pot","bidon","rouleau"};
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);load();show("Caisse");}
 JSONObject item(String name,String cat,String code,String unit,double cost,double price,double stock){JSONObject o=new JSONObject();try{o.put("name",name);o.put("cat",cat);o.put("code",code);o.put("unit",unit);o.put("cost",cost);o.put("price",price);o.put("stock",stock);}catch(Exception e){}return o;}
 void load(){try{JSONObject d=new JSONObject(getPreferences(0).getString("data","{}"));for(String k:new String[]{"products","cart","sales"}){JSONArray a=d.optJSONArray(k);if(a==null)continue;ArrayList<JSONObject> dest=k.equals("products")?products:k.equals("cart")?cart:sales;for(int i=0;i<a.length();i++)dest.add(a.getJSONObject(i));}}catch(Exception e){} if(products.isEmpty()&&getPreferences(0).getBoolean("firstLaunch",true)){seedProducts();getPreferences(0).edit().putBoolean("firstLaunch",false).apply();} if(!getPreferences(0).getBoolean("defaultStock100V1",false)){for(JSONObject p:products){if(p.optString("code").startsWith("INT")&&p.optDouble("stock")==0&&!p.has("initial")){try{p.put("stock",100);p.put("initial",100);}catch(Exception ex){}}}save();getPreferences(0).edit().putBoolean("defaultStock100V1",true).apply();} if(!getPreferences(0).getBoolean("pricesClearedV1",false)){for(JSONObject p:products){if(p.optString("code").startsWith("INT")){try{p.put("price",0);}catch(Exception e){}}}save();getPreferences(0).edit().putBoolean("pricesClearedV1",true).apply();}}
 void seedProducts(){
 String[][] entries={
 {"Viandes","Bœuf (steak)","kg","18.90"},{"Viandes","Bœuf haché","kg","13.90"},{"Viandes","Côte de bœuf","kg","24.90"},{"Viandes","Entrecôte","kg","25.90"},{"Viandes","Agneau","kg","22.90"},{"Viandes","Côtelettes d'agneau","kg","24.90"},{"Viandes","Merguez","kg","12.90"},{"Viandes","Chipolatas","kg","11.90"},{"Viandes","Poulet entier","kg","6.90"},{"Viandes","Blanc de poulet","kg","13.90"},{"Viandes","Cuisses de poulet","kg","8.90"},{"Viandes","Escalopes de dinde","kg","12.90"},
 {"Poissons","Saumon","kg","24.90"},{"Poissons","Cabillaud","kg","21.90"},{"Poissons","Dorade","kg","16.90"},{"Poissons","Sardines","kg","7.90"},{"Poissons","Crevettes","kg","19.90"},{"Poissons","Thon","kg","23.90"},{"Poissons","Merlu","kg","13.90"},{"Poissons","Moules","kg","6.90"},
 {"Fruits & Légumes","Pommes","kg","2.90"},{"Fruits & Légumes","Bananes","kg","2.20"},{"Fruits & Légumes","Oranges","kg","2.80"},{"Fruits & Légumes","Fraises","barquette","3.90"},{"Fruits & Légumes","Tomates","kg","3.50"},{"Fruits & Légumes","Pommes de terre","kg","1.80"},{"Fruits & Légumes","Carottes","kg","1.90"},{"Fruits & Légumes","Oignons","kg","1.60"},{"Fruits & Légumes","Salade","pièce","1.50"},
 {"Aliments & Épicerie","Riz","kg","2.90"},{"Aliments & Épicerie","Pâtes","paquet","1.60"},{"Aliments & Épicerie","Farine","kg","1.20"},{"Aliments & Épicerie","Sucre","kg","1.50"},{"Aliments & Épicerie","Huile","L","3.20"},{"Aliments & Épicerie","Conserves","boîte","2.10"},{"Aliments & Épicerie","Couscous","kg","3.50"},{"Aliments & Épicerie","Café","paquet","4.50"},
 {"Produits laitiers & Fromages","Lait","L","1.30"},{"Produits laitiers & Fromages","Yaourt","pièce","0.70"},{"Produits laitiers & Fromages","Beurre","pièce","2.80"},{"Produits laitiers & Fromages","Emmental","kg","12.90"},{"Produits laitiers & Fromages","Camembert","pièce","2.90"},{"Produits laitiers & Fromages","Crème fraîche","pot","1.80"},
 {"Boissons","Eau minérale","bouteille","0.80"},{"Boissons","Eau gazeuse","bouteille","1.10"},{"Boissons","Jus d'orange","L","2.50"},{"Boissons","Soda cola","canette","1.30"},{"Boissons","Limonade","bouteille","1.90"},{"Boissons","Thé glacé","bouteille","1.80"},
 {"Boulangerie & Viennoiserie","Baguette","pièce","1.20"},{"Boulangerie & Viennoiserie","Pain complet","pièce","2.30"},{"Boulangerie & Viennoiserie","Croissant","pièce","1.30"},{"Boulangerie & Viennoiserie","Pain au chocolat","pièce","1.40"},{"Boulangerie & Viennoiserie","Brioche","pièce","3.80"},
 {"Produits d'entretien","Lessive","bidon","8.90"},{"Produits d'entretien","Liquide vaisselle","bouteille","2.40"},{"Produits d'entretien","Éponge","pièce","1.20"},{"Produits d'entretien","Nettoyant sol","bouteille","3.60"},{"Produits d'entretien","Sacs poubelle","rouleau","2.80"},
 {"Bricolage","Marteau","pièce","12.90"},{"Bricolage","Tournevis","pièce","5.90"},{"Bricolage","Vis","boîte","4.20"},{"Bricolage","Clous","boîte","3.80"},{"Bricolage","Ruban adhésif","rouleau","2.90"},
 {"Électricité","Ampoule LED","pièce","4.90"},{"Électricité","Prise","pièce","5.50"},{"Électricité","Rallonge","pièce","9.90"},{"Électricité","Pile AA","paquet","4.50"},{"Électricité","Interrupteur","pièce","6.90"},
 {"Autres produits","Sac réutilisable","pièce","1.50"},{"Autres produits","Papier cadeau","rouleau","2.20"},{"Autres produits","Pile bouton","pièce","2.90"}
 };
 int i=10001;for(String[] e:entries){JSONObject p=item(e[1],e[0],"INT"+(i++),e[2],0,0,100);try{p.put("initial",100);}catch(Exception ex){}products.add(p);}save();
 }
  void save(){try{JSONObject d=new JSONObject();d.put("products",new JSONArray(products));d.put("cart",new JSONArray(cart));d.put("sales",new JSONArray(sales));getPreferences(0).edit().putString("data",d.toString()).apply();}catch(Exception e){}}
 LinearLayout col(){LinearLayout x=new LinearLayout(this);x.setOrientation(1);return x;}
 LinearLayout row(){LinearLayout x=new LinearLayout(this);x.setOrientation(0);return x;}
 int lighten(int c){return Color.rgb(Math.min(255,Color.red(c)+20),Math.min(255,Color.green(c)+20),Math.min(255,Color.blue(c)+20));}
 int shade(int c){return Color.rgb(Math.max(0,Color.red(c)-18),Math.max(0,Color.green(c)-18),Math.max(0,Color.blue(c)-18));}
 GradientDrawable bg(int c){GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{lighten(c),c,shade(c)});g.setCornerRadius(10);g.setStroke(1,0xffa9b9cc);return g;}
 TextView txt(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);t.setPadding(5,5,5,5);return t;}
 Button btn(String s,int c,Runnable action){Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(12);b.setMinHeight(0);b.setMinimumHeight(0);b.setTextColor(Color.WHITE);b.setBackground(bg(c));b.setPadding(5,6,5,6);b.setOnClickListener(v->action.run());return b;}
 void add(LinearLayout p,View v,float w){p.addView(v,new LinearLayout.LayoutParams(0,-2,w));}
 LinearLayout panel(LinearLayout p,String title){LinearLayout x=col();x.setPadding(5,5,5,5);x.setBackground(bg(0xffe9f1fb));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(5,5,5,5);p.addView(x,lp);TextView h=txt(title,16,Color.WHITE);h.setTypeface(null,Typeface.BOLD);h.setBackground(bg(navy));x.addView(h);return x;}
 EditText field(String hint,String val){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setText(val);e.setTextSize(18);e.setBackground(bg(Color.WHITE));e.setPadding(10,10,10,10);return e;}
 Spinner spin(String[] a){Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,a));return s;}
 double num(String s){try{return Double.parseDouble(s.replace(',','.'));}catch(Exception e){return 0;}}
 String eur(double v){return String.format(Locale.FRANCE,"%.2f €",v);}
 JSONObject find(String code){for(JSONObject p:products)if(!code.isEmpty()&&p.optString("code").equals(code))return p;return null;}
 double lineNet(JSONObject c){
 double qty=Math.max(0,c.optDouble("qty")),unitPrice=Math.max(0,c.optDouble("price"));
 double discount=Math.max(0,Math.min(100,c.optDouble("discount",0)));
 double second=Math.max(0,Math.min(100,c.optDouble("secondDiscount",0)));
 double gross=qty*unitPrice;
 double targetDiscount=Math.max(0,Math.min(100,c.optDouble("targetDiscount",0)));
 int targetIndex=c.optInt("targetIndex",1);
 String targetUnit=c.optString("unit","");
 boolean targetDiscrete=!(targetUnit.equals("kg")||targetUnit.equals("g")||targetUnit.equals("L")||targetUnit.equals("mL"));
 double targetReduction=targetDiscrete&&targetIndex>=1&&qty>=targetIndex?unitPrice*targetDiscount/100.0:0;
 double afterIndividual=gross*(1-discount/100.0);
 if(second<=0||qty<2)return Math.max(0,afterIndividual-targetReduction);
 String unit=c.optString("unit","");
 boolean discrete=!(unit.equals("kg")||unit.equals("g")||unit.equals("L")||unit.equals("mL"));
 if(!discrete)return Math.max(0,afterIndividual-targetReduction);
 double pairs=Math.floor(qty/2.0);
 return Math.max(0,afterIndividual-pairs*unitPrice*(1-discount/100.0)*second/100.0-targetReduction);
 }
 double subtotal(){double t=0;for(JSONObject c:cart)t+=lineNet(c);return t;}
 double taxRate(){return Math.max(0,Math.min(100,num(getPreferences(0).getString("taxRate","0"))));}
 double tax(){return subtotal()*taxRate()/100.0;}
 double total(){return subtotal()+tax();}
 double stockQty(JSONObject line,JSONObject product){String u=line.optString("unit"),base=product.optString("unit");double q=line.optDouble("qty");if(u.equals(base))return q;if(u.equals("g")&&base.equals("kg"))return q/1000.0;if(u.equals("kg")&&base.equals("g"))return q*1000.0;if(u.equals("mL")&&base.equals("L"))return q/1000.0;if(u.equals("L")&&base.equals("mL"))return q*1000.0;return -1;}
 void renderCaisseCategories(String category){
 if(caisseCategories==null)return;caisseCategories.removeAllViews();
 if(category==null){
 for(int i=0;i<cats.length;i+=4){LinearLayout r=row();caisseCategories.addView(r);for(int j=i;j<Math.min(i+4,cats.length);j++){String c=cats[j];LinearLayout tile=categoryTile(j,()->renderCaisseCategories(c));r.addView(tile,new LinearLayout.LayoutParams(0,Math.max(115,(int)(getResources().getDisplayMetrics().heightPixels*0.17f)),1));}}
 }else{
 caisseCategories.addView(btn("← Toutes les catégories",blue,()->renderCaisseCategories(null)));
 TextView heading=txt("Articles — "+category,21,navy);heading.setTypeface(null,Typeface.BOLD);caisseCategories.addView(heading);
 int count=0;LinearLayout r=null;
 for(JSONObject p:products){if(!category.equals("Tous les produits")&&!category.equals(p.optString("cat")))continue;
 if(count%3==0){r=row();caisseCategories.addView(r);}count++;
 String label=productIcon(p.optString("name"))+"\n"+p.optString("name");
 Button b=btn(label,catColors[Math.max(0,Arrays.asList(cats).indexOf(p.optString("cat")))],()->{
 chosen=p.optString("name");chosenCode=p.optString("code");pendingRemises.clear();pendingDiscount=0;pendingTarget=1;remiseValidee=false;
 if(caisseSelectedName!=null)caisseSelectedName.setText(chosen);
 if(quickDiscount!=null)quickDiscount.setText("0");
 if(discountTarget!=null)discountTarget.setSelection(0);
 if(price!=null)price.setText(p.optDouble("price")>0?String.valueOf(p.optDouble("price")):"");
 if(qty!=null)qty.setText("1");
 if(caisseUnit!=null){int ix=Arrays.asList(units).indexOf(p.optString("unit"));caisseUnit.setSelection(Math.max(0,ix));}
 if(caisseStock!=null)caisseStock.setText(p.optString("name")+" — Stock : "+p.optDouble("stock")+" "+p.optString("unit"));
 });b.setTextSize(16);r.addView(b,new LinearLayout.LayoutParams(0,Math.max(100,(int)(getResources().getDisplayMetrics().heightPixels*0.135f)),1));
 }if(count==0)caisseCategories.addView(txt("Aucun article.",18,navy));
 }
 }
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 void show(String page){
 root=col();root.setBackgroundColor(0xffd5e2f1);setContentView(root);
 LinearLayout top=row();top.setBackgroundColor(0xff061629);root.addView(top);
 add(top,txt("🛒 Caisse Magasin",25,gold),2.4f);
 String[] pages={"Caisse","Ticket","Stock","Produits","Codes-barres","Rapports","Paramètres"};
 String[] symbols={"▣","▤","▤","▦","▥","▥","⚙"};
 for(int i=0;i<pages.length;i++){String n=pages[i];add(top,btn(symbols[i]+" "+n,n.equals(page)?0xff087bd3:0xff061629,()->show(n)),1);}
 LinearLayout nav=row();nav.setBackgroundColor(0xff0b315b);root.addView(nav);
 add(nav,btn("☰ Menu",blue,()->show("Caisse")),1);
 add(nav,btn("← Retour",blue,()->show("Caisse")),1);
 add(nav,txt("● Hors ligne",13,Color.WHITE),2);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
 body=col();body.setLayoutParams(new android.widget.ScrollView.LayoutParams(-1,-1));scroll.addView(body);
 switch(page){case "Caisse":caisse();break;case "Ticket":ticketPage();break;case "Produits":produits(null);break;case "Stock":stock();break;case "Codes-barres":codes();break;case "Rapports":rapports();break;default:parametres();}
 }
 LinearLayout categoryTile(int index,Runnable action){
 LinearLayout tile=col();tile.setGravity(Gravity.CENTER);tile.setBackground(bg(catColors[index]));tile.setPadding(3,3,3,3);
 TextView image=txt(catIcons[index],42,Color.WHITE);image.setGravity(Gravity.CENTER);tile.addView(image,new LinearLayout.LayoutParams(-1,0,1.2f));
 TextView title=txt(cats[index],17,Color.WHITE);title.setTypeface(null,Typeface.BOLD);title.setGravity(Gravity.CENTER);
 tile.addView(title,new LinearLayout.LayoutParams(-1,0,1));
 tile.setOnClickListener(v->action.run());return tile;
 }
 void caisse(){
 LinearLayout main=row();main.setMinimumHeight((int)(getResources().getDisplayMetrics().heightPixels*0.84f));body.addView(main,new LinearLayout.LayoutParams(-1,-2));
 LinearLayout left=col(),right=col();main.addView(left,new LinearLayout.LayoutParams(0,-2,1.04f));main.addView(right,new LinearLayout.LayoutParams(0,-2,1.1f));
 LinearLayout categories=panel(left,"▤ Catégories de produits");
 caisseCategories=col();categories.addView(caisseCategories);renderCaisseCategories(null);

 LinearLayout discountPanel=panel(left,"▤ Produit sélectionné et remise");
 caisseSelectedName=txt(chosen.isEmpty()?"Choisissez un produit dans les catégories":chosen,19,navy);
 discountPanel.addView(caisseSelectedName);
 LinearLayout selectedRow=row();discountPanel.addView(selectedRow);
 add(selectedRow,txt("Quantité / unité :",16,navy),1.2f);
 caisseSelectedQty=txt("1 pièce",17,navy);add(selectedRow,caisseSelectedQty,1);
 add(selectedRow,txt("Unité à remiser :",16,navy),1.3f);
 String[] targets={"01","02","03","04","05","06","07","08","09","10"};
 discountTarget=spin(targets);discountTarget.setSelection(Math.max(0,Math.min(9,pendingTarget-1)));add(selectedRow,discountTarget,0.7f);
 LinearLayout remiseRow=row();discountPanel.addView(remiseRow);
 add(remiseRow,txt("Remise (%) :",17,navy),1);
 quickDiscount=field("Taux de remise",""+pendingDiscount);
 quickDiscount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
 add(remiseRow,quickDiscount,1);
 add(remiseRow,btn("Valider la remise",green,()->{
  double d=num(quickDiscount.getText().toString());
  if(d<0||d>100){toast("Remise entre 0 et 100 %");return;}
  pendingDiscount=d;pendingTarget=discountTarget.getSelectedItemPosition()+1;remiseValidee=true;pendingRemises.put(pendingTarget,d);
  toast("Remises mémorisées : "+pendingRemises.size()+" | N° "+pendingTarget+" : "+d+" %");
 }),1.5f);
 LinearLayout calc=panel(right,"▣ Calculatrice");
 LinearLayout entry=row();calc.addView(entry);
 JSONObject selected=find(chosenCode);
 LinearLayout fields=col();entry.addView(fields,new LinearLayout.LayoutParams(0,-2,1));
 fields.addView(txt("Qté",13,navy));qty=field("Quantité","1");fields.addView(qty);
 fields.addView(txt("Unité",13,navy));caisseUnit=spin(units);fields.addView(caisseUnit);if(selected!=null)caisseUnit.setSelection(Math.max(0,Arrays.asList(units).indexOf(selected.optString("unit"))));
 fields.addView(txt("Prix unitaire",13,navy));price=field("Prix à saisir",selected==null||selected.optDouble("price")==0?"":String.valueOf(selected.optDouble("price")));fields.addView(price);
 TextView lineTotal=txt("Total article : "+eur(num(qty.getText().toString())*num(price.getText().toString())),19,green);lineTotal.setBackground(bg(0xffc7f4d0));fields.addView(lineTotal);android.text.TextWatcher watcher=new android.text.TextWatcher(){public void beforeTextChanged(CharSequence a,int st,int c,int after){}public void onTextChanged(CharSequence a,int st,int before,int count){lineTotal.setText("Total article : "+eur(num(qty.getText().toString())*num(price.getText().toString())));}public void afterTextChanged(android.text.Editable e){}};qty.addTextChangedListener(watcher);price.addTextChangedListener(watcher); android.text.TextWatcher qWatcher=new android.text.TextWatcher(){public void beforeTextChanged(CharSequence t,int st,int c,int af){}public void onTextChanged(CharSequence t,int st,int before,int count){caisseSelectedQty.setText(qty.getText().toString()+" "+caisseUnit.getSelectedItem());}public void afterTextChanged(android.text.Editable e){}};qty.addTextChangedListener(qWatcher);caisseUnit.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){caisseSelectedQty.setText(qty.getText().toString()+" "+units[pos]);}});
 LinearLayout keypad=col();entry.addView(keypad,new LinearLayout.LayoutParams(0,-2,1.7f));
 String[] keys={"7","8","9","←","4","5","6","×","1","2","3","+","0",",","−","Ajouter"};
 for(int i=0;i<keys.length;i+=4){LinearLayout r=row();keypad.addView(r);for(int j=i;j<i+4;j++){String k=keys[j];Button touch=btn(k,k.equals("Ajouter")?green:k.equals("←")?0xffe21c22:navy,()->key(k));touch.setTextSize(k.equals("Ajouter")?18:29);touch.setTypeface(null,Typeface.BOLD);LinearLayout.LayoutParams touchSize=new LinearLayout.LayoutParams(0,76,1);touchSize.setMargins(2,2,2,2);r.addView(touch,touchSize);}}
 LinearLayout pay=panel(right,"▣ Paiement");
 LinearLayout r=row();pay.addView(r);
 String[] methods={"Carte bancaire","Espèces","Chèque bancaire","Chèque restaurant"};
 String[] icons={"💳","💶","✍","🍴"};
 int[] payColors={0xff1954d2,0xff159c2d,0xff852ac3,0xffef7900};
 for(int j=0;j<methods.length;j++){String method=methods[j];Button payment=btn(icons[j]+"\n"+method,payColors[j],()->pay(method));payment.setTextSize(19);payment.setTypeface(null,Typeface.BOLD);payment.setGravity(Gravity.CENTER);r.addView(payment,new LinearLayout.LayoutParams(0,Math.max(92,(int)(getResources().getDisplayMetrics().heightPixels*0.13f)),1));}
 }
 void key(String k){if(k.equals("Ajouter")){double q=num(qty.getText().toString()),p=num(price.getText().toString());if(q<=0||p<0||price.getText().toString().trim().isEmpty()){toast("Saisir une quantité et un prix valides");return;}JSONObject selected=find(chosenCode);
 if(selected==null){toast("Sélectionnez une catégorie puis un article");return;}
 boolean discrete=!Arrays.asList("kg","g","L","mL").contains(caisseUnit.getSelectedItem().toString());
 if(!pendingRemises.isEmpty()&&(!discrete||q!=Math.floor(q))){toast("Les remises numérotées nécessitent des unités entières");return;}
 for(Integer ix:pendingRemises.keySet())if(ix>q){toast("Unité n° "+ix+" supérieure à la quantité");return;}
 if(selected!=null){double existing=0;for(JSONObject c:cart)if(c.optString("code").equals(chosenCode))existing+=Math.max(0,stockQty(c,selected));double stockQ=stockQty(item("", "", "",caisseUnit.getSelectedItem().toString(),0,0,0),selected);if(stockQ<0){toast("Unité incompatible avec le stock de ce produit");return;}double factor=caisseUnit.getSelectedItem().toString().equals(selected.optString("unit"))?1:((caisseUnit.getSelectedItem().toString().equals("g")||caisseUnit.getSelectedItem().toString().equals("mL"))?0.001:1000);if(q*factor+existing>selected.optDouble("stock")){toast("Stock insuffisant");return;}}if(pendingRemises.isEmpty()){
 JSONObject c=item(chosen,selected.optString("cat"),chosenCode,selected.optString("unit"),0,p,0);
 try{c.put("qty",q);c.put("unit",caisseUnit.getSelectedItem().toString());c.put("discount",0);c.put("secondDiscount",0);c.put("targetDiscount",0);c.put("targetIndex",1);}catch(Exception e){}
 cart.add(c);
 }else{
 for(int ix=1;ix<=(int)q;ix++){
 double remise=pendingRemises.containsKey(ix)?pendingRemises.get(ix):0;
 JSONObject c=item(chosen,selected.optString("cat"),chosenCode,selected.optString("unit"),0,p,0);
 try{c.put("qty",1);c.put("unit",caisseUnit.getSelectedItem().toString());c.put("discount",0);c.put("secondDiscount",0);c.put("targetDiscount",remise);c.put("targetIndex",1);c.put("unitNumber",ix);}catch(Exception e){}
 cart.add(c);
 }
 }
 pendingRemises.clear();pendingDiscount=0;pendingTarget=1;remiseValidee=false;chosen="";chosenCode="";save();show("Caisse");toast("Article ajouté à la page 2");return;}
 EditText e=qty.hasFocus()?qty:price;String s=e.getText().toString();if(k.equals("←"))s=s.isEmpty()?"":s.substring(0,s.length()-1);else if(k.equals("+"))s=String.valueOf(num(s)+1);else if(k.equals("−"))s=String.valueOf(Math.max(0,num(s)-1));else if(k.equals("×"))s=String.valueOf(num(s)*2);else s+=k.equals(",")?".":k;e.setText(s);e.setSelection(s.length());}
 void produits(String category){
 body.removeAllViews();
 LinearLayout layout=row();layout.setMinimumHeight((int)(getResources().getDisplayMetrics().heightPixels*0.84f));body.addView(layout,new LinearLayout.LayoutParams(-1,-1));
 LinearLayout side=col(),center=col(),details=col();
 layout.addView(side,new LinearLayout.LayoutParams(0,-1,1.5f));
 layout.addView(center,new LinearLayout.LayoutParams(0,-1,4.3f));
 layout.addView(details,new LinearLayout.LayoutParams(0,-1,2.2f));
 LinearLayout menu=panel(side,"Catégories");
 for(int j=0;j<cats.length;j++){String c=cats[j];menu.addView(btn(catIcons[j]+" "+c,catColors[j],()->produits(c.equals("Tous les produits")?null:c)));}
 LinearLayout list=panel(center,"Produits — "+(category==null?"Tous les produits":category));
 int count=0;LinearLayout grid=null;
 for(JSONObject p:products){
 if(category!=null&&!category.equals(p.optString("cat")))continue;
 if(count%3==0){grid=row();list.addView(grid);}
 count++;
 String label=productIcon(p.optString("name"))+"\n"+p.optString("name")+"\n"+(p.optDouble("price")>0?eur(p.optDouble("price")):"Prix à saisir")+" / "+p.optString("unit");
 int color=catColors[Math.max(0,Arrays.asList(cats).indexOf(p.optString("cat")))];
 Button productButton=btn(label,color,()->{chosen=p.optString("name");chosenCode=p.optString("code");produits(category);});productButton.setTextSize(17);grid.addView(productButton,new LinearLayout.LayoutParams(0,Math.max(120,(int)(getResources().getDisplayMetrics().heightPixels*0.18f)),1));
 }
 if(count==0)list.addView(txt("Aucun produit dans cette catégorie.",18,navy));
 LinearLayout selection=panel(details,"Article sélectionné");
 JSONObject p=find(chosenCode);
 if(p==null){selection.addView(txt("Touchez un produit pour le sélectionner.",18,navy));return;}
 selection.addView(txt(p.optString("name"),23,navy));
 selection.addView(txt("Prix unitaire / "+p.optString("unit"),16,navy));EditText sellingPrice=field("Prix de vente à saisir",p.optDouble("price")>0?String.valueOf(p.optDouble("price")):"");selection.addView(sellingPrice);selection.addView(btn("Modifier le prix",blue,()->{double v=num(sellingPrice.getText().toString());if(sellingPrice.getText().toString().trim().isEmpty()||v<0){toast("Saisir un prix valide");return;}try{p.put("price",v);}catch(Exception e){}save();produits(category);}));
 selection.addView(txt("Stock actuel : "+p.optDouble("stock")+" "+p.optString("unit"),17,navy));
 EditText amount=field("Quantité","1");selection.addView(amount);
 selection.addView(btn("−",blue,()->amount.setText(String.valueOf(Math.max(0,num(amount.getText().toString())-1)))));
 selection.addView(btn("+",blue,()->amount.setText(String.valueOf(num(amount.getText().toString())+1))));
 selection.addView(btn("🛒 Ajouter à la vente",green,()->{
 double q=num(amount.getText().toString());if(q<=0){toast("Quantité incorrecte");return;}if(sellingPrice.getText().toString().trim().isEmpty()){toast("Saisissez le prix de vente");return;}double selectedPrice=num(sellingPrice.getText().toString());if(selectedPrice<0){toast("Prix incorrect");return;}
 double reserved=0;for(JSONObject c:cart)if(c.optString("code").equals(p.optString("code")))reserved+=c.optDouble("qty");
 if(q+reserved>p.optDouble("stock")){toast("Stock insuffisant : renseignez le stock réel dans Stock");return;}
 JSONObject line=item(p.optString("name"),p.optString("cat"),p.optString("code"),p.optString("unit"),p.optDouble("cost"),selectedPrice,0);
 try{line.put("qty",q);}catch(Exception e){}
 cart.add(line);save();show("Caisse");
 }));
 LinearLayout bottom=panel(body,"Stock — "+p.optString("cat"));bottom.addView(txt("Stock initial et mouvements à renseigner dans l'inventaire | Stock disponible : "+p.optDouble("stock")+" "+p.optString("unit"),16,navy));
 }
 void stock(){
 LinearLayout layout=row();body.addView(layout);
 LinearLayout sidebar=col(),main=col();
 layout.addView(sidebar,new LinearLayout.LayoutParams(0,-2,1.25f));
 layout.addView(main,new LinearLayout.LayoutParams(0,-2,5.4f));
 LinearLayout categories=panel(sidebar,"Catégories de produits");
 for(int j=0;j<cats.length;j++){String c=cats[j];categories.addView(btn(catIcons[j]+" "+c,catColors[j],()->stockCategory(c)));}
 LinearLayout form=panel(main,"▤ Gestion du stock — ajouter un article");
 LinearLayout fields=row();form.addView(fields);
 EditText name=field("Nom de l'article",""),purchase=field("Prix d'achat (€)",""),sell=field("Prix de vente (€)",""),quantity=field("Stock initial","100"),barcode=field("Code-barres","");
 Spinner cat=spin(cats),unit=spin(units);
 add(fields,name,3);add(fields,cat,2);add(fields,unit,1);add(fields,purchase,1.3f);add(fields,sell,1.3f);add(fields,quantity,1.2f);
 LinearLayout codes=row();form.addView(codes);add(codes,barcode,3);
 add(codes,btn("▥ Code interne",blue,()->barcode.setText("INT"+System.currentTimeMillis())),1);
 add(codes,btn("✓ Enregistrer",green,()->{
 String n=name.getText().toString().trim(),b=barcode.getText().toString().trim();
 if(n.isEmpty()||b.isEmpty()||find(b)!=null){toast("Nom ou code manquant, ou code déjà utilisé");return;}
 double q=num(quantity.getText().toString()),pr=num(sell.getText().toString()),co=num(purchase.getText().toString());
 if(q<0||pr<0||co<0){toast("Valeur négative interdite");return;}
 JSONObject p=item(n,cat.getSelectedItem().toString(),b,unit.getSelectedItem().toString(),co,pr,q);
 try{p.put("initial",q);}catch(Exception e){}
 products.add(p);save();show("Stock");
 }),1);
 LinearLayout inventory=panel(main,"▤ Inventaire et mouvements de stock");
 LinearLayout headers=row();inventory.addView(headers);
 for(String h:new String[]{"Article","Code-barres","P. achat","P. vente","Stock initial","Sorties","Stock restant","Actions"})add(headers,txt(h,12,navy),1);
 for(JSONObject p:products)stockLine(inventory,p);
 LinearLayout actions=panel(main,"Actions de stock");
 LinearLayout ar=row();actions.addView(ar);
 add(ar,btn("▤ Inventaire",blue,()->show("Stock")),1);
 add(ar,btn("▥ Codes-barres",blue,()->show("Codes-barres")),1);
 add(ar,btn("▣ Historique",blue,()->show("Rapports")),1);
 }
 void stockCategory(String category){
 body.removeAllViews();
 LinearLayout pane=panel(body,"Stock — "+category);
 pane.addView(btn("← Retour à l'inventaire",blue,()->show("Stock")));
 for(JSONObject p:products)if(category.equals("Tous les produits")||category.equals(p.optString("cat")))stockLine(pane,p);
 }
 void stockLine(LinearLayout parent,JSONObject p){
 LinearLayout r=row();parent.addView(r);
 double initial=p.has("initial")?p.optDouble("initial"):p.optDouble("stock");
 add(r,txt(p.optString("name"),12,navy),1.8f);
 add(r,txt(p.optString("code"),12,navy),1.3f);
 add(r,txt(p.optDouble("cost")==0?"—":eur(p.optDouble("cost")),12,navy),1);
 add(r,txt(p.optDouble("price")==0?"À saisir":eur(p.optDouble("price")),12,navy),1);
 add(r,txt(String.valueOf(initial),12,navy),1);
 add(r,txt(String.valueOf(Math.max(0,initial-p.optDouble("stock"))),12,navy),1);
 add(r,txt(p.optDouble("stock")+" "+p.optString("unit"),12,green),1.2f);
 add(r,btn("✎ Modifier",blue,()->edit(p)),1);
 add(r,btn("✕",0xffb02031,()->new AlertDialog.Builder(this).setTitle("Supprimer l'article ?").setMessage(p.optString("name")).setNegativeButton("Annuler",null).setPositiveButton("Supprimer",(d,w)->{products.remove(p);save();show("Stock");}).show()),0.5f);
 }
 void edit(JSONObject p){
 body.removeAllViews();LinearLayout form=panel(body,"✎ Modifier l'article : "+p.optString("name"));
 EditText name=field("Nom",p.optString("name"));
 EditText barcode=field("Code-barres",p.optString("code"));
 EditText pr=field("Prix de vente",p.optDouble("price")==0?"":String.valueOf(p.optDouble("price")));
 EditText cost=field("Prix d'achat",p.optDouble("cost")==0?"":String.valueOf(p.optDouble("cost")));
 EditText q=field("Stock actuel",String.valueOf(p.optDouble("stock")));
 Spinner cat=spin(cats),unit=spin(units);
 int catPos=Arrays.asList(cats).indexOf(p.optString("cat")),unitPos=Arrays.asList(units).indexOf(p.optString("unit"));
 cat.setSelection(Math.max(0,catPos));unit.setSelection(Math.max(0,unitPos));
 LinearLayout r1=row();form.addView(r1);add(r1,name,3);add(r1,cat,2);add(r1,unit,1);
 LinearLayout r2=row();form.addView(r2);add(r2,barcode,2);add(r2,cost,1);add(r2,pr,1);add(r2,q,1);
 form.addView(btn("✓ Enregistrer toutes les modifications",green,()->{
 String n=name.getText().toString().trim(),code=barcode.getText().toString().trim();
 JSONObject duplicate=find(code);
 if(n.isEmpty()||code.isEmpty()||(duplicate!=null&&duplicate!=p)){toast("Nom ou code-barres invalide / déjà utilisé");return;}
 if(num(pr.getText().toString())<0||num(q.getText().toString())<0||num(cost.getText().toString())<0){toast("Valeurs négatives interdites");return;}
 String previousCode=p.optString("code");
 try{
 p.put("name",n);p.put("code",code);p.put("cat",cat.getSelectedItem().toString());p.put("unit",unit.getSelectedItem().toString());
 p.put("price",num(pr.getText().toString()));p.put("cost",num(cost.getText().toString()));p.put("stock",num(q.getText().toString()));
 if(!p.has("initial"))p.put("initial",num(q.getText().toString()));
 }catch(Exception e){}
 if(previousCode.equals(chosenCode)){chosenCode=code;chosen=n;}
 save();show("Stock");
 }));
 form.addView(btn("← Annuler",blue,()->show("Stock")));
 }
 void codes(){
 LinearLayout layout=row();body.addView(layout);
 LinearLayout left=col(),right=col();layout.addView(left,new LinearLayout.LayoutParams(0,-2,1));layout.addView(right,new LinearLayout.LayoutParams(0,-2,1));
 LinearLayout scan=panel(left,"▥ Scanner un code-barres");
 codeField=field("Code-barres : saisie ou lecteur externe","");
 scan.addView(codeField);
 scan.addView(btn("🔍 Rechercher",blue,()->{
 JSONObject x=find(codeField.getText().toString().trim());if(x==null){toast("Code inconnu");return;}
 chosen=x.optString("name");chosenCode=x.optString("code");show("Produits");
 }));
 LinearLayout selected=panel(left,"Produit sélectionné");
 JSONObject p=find(chosenCode);selected.addView(txt(p==null?"Aucun article sélectionné":p.optString("name")+" • "+p.optString("code")+" • "+p.optDouble("stock")+" "+p.optString("unit"),17,navy));
 LinearLayout out=panel(right,"▤ Sorties et mouvements");
 out.addView(txt("Les sorties de stock sont enregistrées à la validation du règlement.",16,navy));
 out.addView(btn("▣ Aller à la caisse",green,()->show("Caisse")));
 LinearLayout history=panel(right,"▤ Derniers règlements");
 int start=Math.max(0,sales.size()-10);
 for(int i=sales.size()-1;i>=start;i--){JSONObject sale=sales.get(i);history.addView(txt(new java.text.SimpleDateFormat("dd/MM HH:mm",Locale.FRANCE).format(new Date(sale.optLong("time")))+"  "+sale.optString("method")+"  "+eur(sale.optDouble("total")),14,navy));}
 }
 void pay(String method){if(cart.isEmpty()){toast("Ticket vide");return;}new AlertDialog.Builder(this).setTitle("Confirmer le règlement").setMessage(method+" — "+eur(total())).setNegativeButton("Annuler",null).setPositiveButton("Valider",(d,w)->{HashMap<String,Double> used=new HashMap<>();for(JSONObject c:cart){String code=c.optString("code");if(!code.isEmpty())used.put(code,used.getOrDefault(code,0.0)+(find(code)==null?c.optDouble("qty"):Math.max(0,stockQty(c,find(code)))));}for(String code:used.keySet()){JSONObject p=find(code);if(p==null||p.optDouble("stock")<used.get(code)){toast("Stock insuffisant : "+code);return;}}for(String code:used.keySet()){JSONObject p=find(code);try{p.put("stock",p.optDouble("stock")-used.get(code));}catch(Exception e){}}JSONObject s=new JSONObject();try{s.put("time",System.currentTimeMillis());s.put("method",method);s.put("total",total());s.put("items",new JSONArray(cart));}catch(Exception e){}sales.add(s);cart.clear();save();show("Caisse");toast("Vente enregistrée");}).show();}
 void editCartLine(JSONObject line){
 LinearLayout form=col();
 EditText quantity=field("Quantité",String.valueOf(line.optDouble("qty")));
 EditText unitPrice=field("Prix unitaire",String.valueOf(line.optDouble("price")));
 EditText target=field("Unité remisée (01 à 10)",String.valueOf(line.optInt("targetIndex",1)));
 EditText reduction=field("Remise ciblée (%)",String.valueOf(line.optDouble("targetDiscount",0)));
 for(EditText e:new EditText[]{quantity,unitPrice,target,reduction}){e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);form.addView(e);}
 new AlertDialog.Builder(this).setTitle("Modifier l'article").setView(form).setNegativeButton("Annuler",null).setPositiveButton("Enregistrer",(d,w)->{
 double q=num(quantity.getText().toString()),p=num(unitPrice.getText().toString()),r=num(reduction.getText().toString()),t=num(target.getText().toString());
 if(q<=0||p<0||r<0||r>100||t<1||t>10||t!=Math.floor(t)){toast("Vérifiez les valeurs");return;}
 try{line.put("qty",q);line.put("price",p);line.put("targetIndex",(int)t);line.put("targetDiscount",r);}catch(Exception e){}
 save();show("Ticket");
 }).show();
 }
 void ticketPage(){
 LinearLayout box=panel(body,"▤ Page 2 — Articles achetés / Ticket en cours");
 LinearLayout header=row();box.addView(header);
 for(String h:new String[]{"Article","Qté / unité","Prix unitaire","Remise %","2ᵉ article %","Total ligne","Actions"})add(header,txt(h,17,navy),1);
 for(JSONObject line:new ArrayList<>(cart)){
 LinearLayout r=row();box.addView(r);
 add(r,txt(line.optString("name")+(line.has("unitNumber")?" — N° "+line.optInt("unitNumber"):""),17,navy),1);
 add(r,txt(String.format(Locale.FRANCE,"%.3f",line.optDouble("qty"))+" "+line.optString("unit"),16,navy),1);
 add(r,txt(eur(line.optDouble("price")),17,navy),1);
 LinearLayout discount= row();add(r,discount,1);
 discount.addView(txt("Remise unité : "+line.optDouble("targetDiscount",0)+" %",14,navy));
 EditText pct=field("Remise %",String.valueOf(line.optDouble("discount",0)));pct.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);add(discount,pct,2);
 add(discount,btn("✓",green,()->{double d=num(pct.getText().toString());if(d<0||d>100){toast("Remise entre 0 et 100 %");return;}try{line.put("discount",d);}catch(Exception e){}save();show("Ticket");}),1);
 LinearLayout secondRow=row();add(r,secondRow,1);
 EditText secondPct=field("2ᵉ - %",String.valueOf(line.optDouble("secondDiscount",0)));
 secondPct.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
 add(secondRow,secondPct,2);
 add(secondRow,btn("✓",green,()->{double d=num(secondPct.getText().toString());if(d<0||d>100){toast("Remise 2ᵉ article entre 0 et 100 %");return;}try{line.put("secondDiscount",d);}catch(Exception e){}save();show("Ticket");}),1);
 add(r,txt(eur(lineNet(line)),18,green),1);
 LinearLayout actions=row();add(r,actions,1);
 add(actions,btn("✕",0xffc51c2d,()->{cart.remove(line);save();show("Ticket");}),1);
 add(actions,btn("Modifier",0xffe88808,()->editCartLine(line)),2);
 }
 if(cart.isEmpty())box.addView(txt("Aucun article ajouté pour le moment.",19,navy));box.addView(txt("Promotion 2ᵉ article : réduction appliquée à chaque deuxième unité achetée du même article, pour les unités à la pièce (pas au poids ni au litre). Les remises se cumulent.",14,navy));
 LinearLayout totals=panel(body,"Total des articles, TVA et remises");
 totals.addView(txt("Sous-total après remises : "+eur(subtotal()),22,navy));
 LinearLayout rateRow=row();totals.addView(rateRow);
 add(rateRow,txt("TVA (%) :",19,navy),1);
 EditText rate=field("Taux de TVA à définir",getPreferences(0).getString("taxRate",""));rate.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);add(rateRow,rate,2);
 add(rateRow,btn("Appliquer la TVA",blue,()->{String v=rate.getText().toString().trim();if(v.isEmpty()||num(v)<0||num(v)>100){toast("Indiquez un taux de TVA entre 0 et 100 %");return;}getPreferences(0).edit().putString("taxRate",v).apply();show("Ticket");}),1);
 totals.addView(txt(getPreferences(0).getString("taxRate","").isEmpty()?"TVA : taux non défini (aucune TVA ajoutée)":"TVA calculée : "+eur(tax()),21,navy));
 TextView sum=txt("TOTAL À PAYER : "+eur(total()),25,green);sum.setTypeface(null,Typeface.BOLD);sum.setBackground(bg(0xffb4f3bf));totals.addView(sum);
 totals.addView(btn("Valider les articles",green,()->validateArticles()));totals.addView(btn("← Revenir à la caisse",blue,()->show("Caisse")));
 }
 String ticketText(){
 StringBuilder out=new StringBuilder("CAISSE MAGASIN\\nTicket des articles\\n\\n");
 for(JSONObject line:cart)out.append(line.optString("name")).append(" | ").append(line.optDouble("qty")).append(" ").append(line.optString("unit")).append(" | PU ").append(eur(line.optDouble("price"))).append(" | remise ").append(line.optDouble("discount")).append("% | unité ").append(line.optInt("targetIndex",1)).append(" remise ").append(line.optDouble("targetDiscount")).append("% | 2e ").append(line.optDouble("secondDiscount")).append("% | ").append(eur(lineNet(line))).append("\\n");
 out.append("\\nSous-total : ").append(eur(subtotal())).append("\\nTVA (").append(taxRate()).append("%) : ").append(eur(tax())).append("\\nTOTAL TTC : ").append(eur(total()));
 return out.toString().replace("\\n","\\n");
 }
 void validateArticles(){
 if(cart.isEmpty()){toast("Aucun article à valider");return;}
 new AlertDialog.Builder(this).setTitle("Valider les articles").setItems(new String[]{"Imprimer","Enregistrer","Imprimer et enregistrer"},(dialog,choice)->{
  if(choice==0||choice==2)printTicket();
  if(choice==1||choice==2)saveTicketDocument();
 }).setNegativeButton("Annuler",null).show();
 }
 void saveTicketDocument(){
 android.content.Intent intent=new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
 intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);intent.setType("text/plain");
 intent.putExtra(android.content.Intent.EXTRA_TITLE,"Ticket-Caisse-Magasin-"+System.currentTimeMillis()+".txt");
 startActivityForResult(intent,7301);
 }
 @Override protected void onActivityResult(int requestCode,int resultCode,android.content.Intent data){
 super.onActivityResult(requestCode,resultCode,data);
 if(requestCode==7301&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
  try(java.io.OutputStream stream=getContentResolver().openOutputStream(data.getData())){
   if(stream==null)throw new java.io.IOException("Fichier inaccessible");
   stream.write(ticketText().getBytes(java.nio.charset.StandardCharsets.UTF_8));
   toast("Ticket enregistré");
  }catch(Exception e){toast("Enregistrement impossible : "+e.getMessage());}
 }
 }
 void printTicket(){
 android.webkit.WebView web=new android.webkit.WebView(this);
 String html="<html><head><meta charset='utf-8'></head><body><pre style='font-size:15px;white-space:pre-wrap'>"+android.text.TextUtils.htmlEncode(ticketText())+"</pre></body></html>";
 web.loadDataWithBaseURL(null,html,"text/html","UTF-8",null);
 web.setWebViewClient(new android.webkit.WebViewClient(){
  @Override public void onPageFinished(android.webkit.WebView view,String url){
   android.print.PrintManager manager=(android.print.PrintManager)getSystemService(PRINT_SERVICE);
   if(manager!=null)manager.print("Ticket Caisse Magasin",view.createPrintDocumentAdapter("Ticket Caisse Magasin"),new android.print.PrintAttributes.Builder().build());
  }
 });
 }
 void rapports(){LinearLayout p=panel(body,"Rapports et historique");double sum=0;for(JSONObject s:sales)sum+=s.optDouble("total");p.addView(txt("Nombre de ventes : "+sales.size()+"\nChiffre d'affaires : "+eur(sum)+"\nProduits : "+products.size(),23,navy));for(JSONObject s:sales)p.addView(txt(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE).format(new Date(s.optLong("time")))+" — "+s.optString("method")+" — "+eur(s.optDouble("total")),16,navy));}
 void parametres(){LinearLayout p=panel(body,"Paramètres");p.addView(txt("Les articles et les ventes sont sauvegardés localement hors ligne. Les moyens de paiement sont enregistrés, sans encaissement bancaire électronique.",19,navy));}
}
