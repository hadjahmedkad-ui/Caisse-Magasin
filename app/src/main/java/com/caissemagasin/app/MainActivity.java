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
 LinearLayout root,body; EditText qty,price,codeField; String chosen="",chosenCode="";
 ArrayList<JSONObject> products=new ArrayList<>(),cart=new ArrayList<>(),sales=new ArrayList<>();
 String[] cats={"Viandes","Poissons","Fruits & Légumes","Aliments & Épicerie","Produits laitiers & Fromages","Boissons","Boulangerie & Viennoiserie","Produits d'entretien","Bricolage","Électricité","Autres produits","Tous les produits"};
 String[] catIcons={"🥩","🐟","🥦","🛒","🧀","🥤","🥐","🧴","🛠","💡","▦","🛍"};
 int[] catColors={0xffad1723,0xff1669a5,0xff258535,0xffdb8a00,0xffd19b16,0xffd43327,0xffa85718,0xff168e9b,0xffbd741e,0xff244d9b,0xff304f86,0xffdf9a06};
 String[] units={"pièce","kg","g","L","mL","bouteille","canette","boîte","carton","sachet","pack","paquet","barquette","pot","bidon","rouleau"};
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);load();show("Caisse");}
 JSONObject item(String name,String cat,String code,String unit,double cost,double price,double stock){JSONObject o=new JSONObject();try{o.put("name",name);o.put("cat",cat);o.put("code",code);o.put("unit",unit);o.put("cost",cost);o.put("price",price);o.put("stock",stock);}catch(Exception e){}return o;}
 void load(){try{JSONObject d=new JSONObject(getPreferences(0).getString("data","{}"));for(String k:new String[]{"products","cart","sales"}){JSONArray a=d.optJSONArray(k);if(a==null)continue;ArrayList<JSONObject> dest=k.equals("products")?products:k.equals("cart")?cart:sales;for(int i=0;i<a.length();i++)dest.add(a.getJSONObject(i));}}catch(Exception e){} if(products.isEmpty()&&getPreferences(0).getBoolean("firstLaunch",true)){seedProducts();getPreferences(0).edit().putBoolean("firstLaunch",false).apply();} if(!getPreferences(0).getBoolean("pricesClearedV1",false)){for(JSONObject p:products){if(p.optString("code").startsWith("INT")){try{p.put("price",0);}catch(Exception e){}}}save();getPreferences(0).edit().putBoolean("pricesClearedV1",true).apply();}}
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
 int i=10001;for(String[] e:entries){JSONObject p=item(e[1],e[0],"INT"+(i++),e[2],0,0,0);products.add(p);}save();
 }
  void save(){try{JSONObject d=new JSONObject();d.put("products",new JSONArray(products));d.put("cart",new JSONArray(cart));d.put("sales",new JSONArray(sales));getPreferences(0).edit().putString("data",d.toString()).apply();}catch(Exception e){}}
 LinearLayout col(){LinearLayout x=new LinearLayout(this);x.setOrientation(1);return x;}
 LinearLayout row(){LinearLayout x=new LinearLayout(this);x.setOrientation(0);return x;}
 GradientDrawable bg(int c){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(12);g.setStroke(1,0xff9fb2c9);return g;}
 TextView txt(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);t.setPadding(6,6,6,6);return t;}
 Button btn(String s,int c,Runnable action){Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(13);b.setMinHeight(0);b.setMinimumHeight(0);b.setTextColor(Color.WHITE);b.setBackground(bg(c));b.setPadding(5,6,5,6);b.setOnClickListener(v->action.run());return b;}
 void add(LinearLayout p,View v,float w){p.addView(v,new LinearLayout.LayoutParams(0,-2,w));}
 LinearLayout panel(LinearLayout p,String title){LinearLayout x=col();x.setPadding(8,8,8,8);x.setBackground(bg(0xfff1f6fc));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(5,5,5,5);p.addView(x,lp);TextView h=txt(title,21,Color.WHITE);h.setTypeface(null,Typeface.BOLD);h.setBackground(bg(navy));x.addView(h);return x;}
 EditText field(String hint,String val){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setText(val);e.setTextSize(18);e.setBackground(bg(Color.WHITE));e.setPadding(10,10,10,10);return e;}
 Spinner spin(String[] a){Spinner s=new Spinner(this);s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,a));return s;}
 double num(String s){try{return Double.parseDouble(s.replace(',','.'));}catch(Exception e){return 0;}}
 String eur(double v){return String.format(Locale.FRANCE,"%.2f €",v);}
 JSONObject find(String code){for(JSONObject p:products)if(!code.isEmpty()&&p.optString("code").equals(code))return p;return null;}
 double total(){double t=0;for(JSONObject c:cart)t+=c.optDouble("qty")*c.optDouble("price");return t;}
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 void show(String page){
 root=col();root.setBackgroundColor(0xffd5e2f1);setContentView(root);
 LinearLayout top=row();top.setBackgroundColor(0xff061629);root.addView(top);
 add(top,txt("🛒 Grand Magasin",22,gold),2.4f);
 String[] pages={"Caisse","Stock","Produits","Codes-barres","Rapports","Paramètres"};
 String[] symbols={"▣","▤","▦","▥","▥","⚙"};
 for(int i=0;i<pages.length;i++){String n=pages[i];add(top,btn(symbols[i]+"\n"+n,n.equals(page)?0xff087bd3:0xff061629,()->show(n)),1);}
 LinearLayout nav=row();nav.setBackgroundColor(0xff0b315b);root.addView(nav);
 add(nav,btn("☰ Menu",blue,()->show("Caisse")),1);
 add(nav,btn("← Retour",blue,()->show("Caisse")),1);
 add(nav,txt("● Hors ligne  |  Caisse Magasin",13,Color.WHITE),3);
 ScrollView scroll=new ScrollView(this);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
 body=col();scroll.addView(body);
 switch(page){case "Caisse":caisse();break;case "Produits":produits(null);break;case "Stock":stock();break;case "Codes-barres":codes();break;case "Rapports":rapports();break;default:parametres();}
 }
 void caisse(){
 LinearLayout main=row();body.addView(main);
 LinearLayout left=col(),right=col();main.addView(left,new LinearLayout.LayoutParams(0,-2,1.04f));main.addView(right,new LinearLayout.LayoutParams(0,-2,1.1f));
 LinearLayout categories=panel(left,"▤ Catégories de produits");
 for(int i=0;i<cats.length;i+=4){LinearLayout r=row();categories.addView(r);for(int j=i;j<Math.min(i+4,cats.length);j++){String c=cats[j];int color=catColors[j];add(r,btn(catIcons[j]+"\n"+c,color,()->{show("Produits");produits(c.equals("Tous les produits")?null:c);}),1);}}
 LinearLayout search=panel(left,"▥ Recherche rapide");
 EditText query=field("Rechercher un produit (nom ou code-barres)","");search.addView(query);
 search.addView(btn("🔍 Rechercher",blue,()->{String q=query.getText().toString().trim().toLowerCase(Locale.ROOT);for(JSONObject p:products)if(p.optString("name").toLowerCase(Locale.ROOT).contains(q)||p.optString("code").toLowerCase(Locale.ROOT).equals(q)){chosen=p.optString("name");chosenCode=p.optString("code");show("Produits");return;}toast("Produit introuvable");}));
 LinearLayout stock=panel(left,"▣ Stock en temps réel");
 JSONObject selected=find(chosenCode);stock.addView(txt(selected==null?"Choisissez un article":selected.optString("name")+"  •  "+selected.optDouble("stock")+" "+selected.optString("unit"),15,navy));
 LinearLayout ticket=panel(right,"▤ Ticket de vente (en cours)");
 LinearLayout headers=row();ticket.addView(headers);for(String h:new String[]{"Article","Qté","P.U.","Total"})add(headers,txt(h,13,navy),1);
 for(JSONObject c:new ArrayList<>(cart)){LinearLayout r=row();ticket.addView(r);add(r,txt(c.optString("name"),13,navy),2);add(r,txt(String.valueOf(c.optDouble("qty")),13,navy),1);add(r,txt(eur(c.optDouble("price")),13,navy),1);add(r,txt(eur(c.optDouble("qty")*c.optDouble("price")),13,navy),1);add(r,btn("✕",0xffbd172b,()->{cart.remove(c);save();show("Caisse");}),0.5f);}
 ticket.addView(txt("TOTAL TTC : "+eur(total()),21,green));
 LinearLayout calc=panel(right,"▣ Calculatrice");
 LinearLayout entry=row();calc.addView(entry);
 LinearLayout fields=col();entry.addView(fields,new LinearLayout.LayoutParams(0,-2,1));
 fields.addView(txt("Qté",13,navy));qty=field("Quantité","1");fields.addView(qty);
 fields.addView(txt("Prix unitaire",13,navy));price=field("Prix à saisir",selected==null||selected.optDouble("price")==0?"":String.valueOf(selected.optDouble("price")));fields.addView(price);
 fields.addView(txt("Total article : "+eur(selected==null?0:selected.optDouble("price")),13,navy));
 LinearLayout keypad=col();entry.addView(keypad,new LinearLayout.LayoutParams(0,-2,2));
 String[] keys={"7","8","9","←","4","5","6","×","1","2","3","+","0",",","−","Ajouter"};
 for(int i=0;i<keys.length;i+=4){LinearLayout r=row();keypad.addView(r);for(int j=i;j<i+4;j++){String k=keys[j];add(r,btn(k,k.equals("Ajouter")?green:navy,()->key(k)),1);}}
 LinearLayout pay=panel(right,"▣ Paiement");
 LinearLayout r=row();pay.addView(r);
 String[] methods={"Carte bancaire","Espèces","Chèque bancaire","Chèque restaurant"};
 String[] icons={"▣","▤","▥","▦"};
 int[] payColors={0xff1954d2,0xff159c2d,0xff852ac3,0xffef7900};
 for(int j=0;j<methods.length;j++){String method=methods[j];add(r,btn(icons[j]+"\n"+method,payColors[j],()->pay(method)),1);}
 }
 void key(String k){if(k.equals("Ajouter")){double q=num(qty.getText().toString()),p=num(price.getText().toString());if(q<=0||p<0){toast("Quantité ou prix incorrect");return;}JSONObject selected=find(chosenCode);if(selected!=null){double existing=0;for(JSONObject c:cart)if(c.optString("code").equals(chosenCode))existing+=c.optDouble("qty");if(q+existing>selected.optDouble("stock")){toast("Stock insuffisant");return;}}JSONObject c=item(chosen.isEmpty()?"Article manuel":chosen,"",chosenCode,"pièce",0,p,0);try{c.put("qty",q);}catch(Exception e){}cart.add(c);save();show("Caisse");return;}
 EditText e=qty.hasFocus()?qty:price;String s=e.getText().toString();if(k.equals("←"))s=s.isEmpty()?"":s.substring(0,s.length()-1);else if(k.equals("+"))s=String.valueOf(num(s)+1);else if(k.equals("−"))s=String.valueOf(Math.max(0,num(s)-1));else if(k.equals("×"))s=String.valueOf(num(s)*2);else s+=k.equals(",")?".":k;e.setText(s);e.setSelection(s.length());}
 void produits(String category){
 body.removeAllViews();
 LinearLayout layout=row();body.addView(layout);
 LinearLayout side=col(),center=col(),details=col();
 layout.addView(side,new LinearLayout.LayoutParams(0,-2,1.5f));
 layout.addView(center,new LinearLayout.LayoutParams(0,-2,4.3f));
 layout.addView(details,new LinearLayout.LayoutParams(0,-2,2.2f));
 LinearLayout menu=panel(side,"Catégories");
 for(int j=0;j<cats.length;j++){String c=cats[j];menu.addView(btn(catIcons[j]+" "+c,catColors[j],()->produits(c.equals("Tous les produits")?null:c)));}
 LinearLayout list=panel(center,"Produits — "+(category==null?"Tous les produits":category));
 int count=0;LinearLayout grid=null;
 for(JSONObject p:products){
 if(category!=null&&!category.equals(p.optString("cat")))continue;
 if(count%3==0){grid=row();list.addView(grid);}
 count++;
 String label="▣\n"+p.optString("name")+"\n"+(p.optDouble("price")>0?eur(p.optDouble("price")):"Prix à saisir")+" / "+p.optString("unit");
 int color=catColors[Math.max(0,Arrays.asList(cats).indexOf(p.optString("cat")))];
 add(grid,btn(label,color,()->{chosen=p.optString("name");chosenCode=p.optString("code");produits(category);}),1);
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
 EditText name=field("Nom de l'article",""),purchase=field("Prix d'achat (€)",""),sell=field("Prix de vente (€)",""),quantity=field("Stock initial",""),barcode=field("Code-barres","");
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
 body.removeAllViews();LinearLayout form=panel(body,"Modifier l'article : "+p.optString("name"));
 EditText name=field("Nom",p.optString("name")),pr=field("Prix de vente",p.optDouble("price")==0?"":String.valueOf(p.optDouble("price"))),cost=field("Prix d'achat",p.optDouble("cost")==0?"":String.valueOf(p.optDouble("cost"))),q=field("Stock actuel",String.valueOf(p.optDouble("stock")));
 form.addView(name);form.addView(pr);form.addView(cost);form.addView(q);
 form.addView(btn("✓ Enregistrer les modifications",green,()->{
 if(name.getText().toString().trim().isEmpty()||num(pr.getText().toString())<0||num(q.getText().toString())<0||num(cost.getText().toString())<0){toast("Valeurs incorrectes");return;}
 try{p.put("name",name.getText().toString().trim());p.put("price",num(pr.getText().toString()));p.put("cost",num(cost.getText().toString()));p.put("stock",num(q.getText().toString()));}catch(Exception e){}
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
 void pay(String method){if(cart.isEmpty()){toast("Ticket vide");return;}new AlertDialog.Builder(this).setTitle("Confirmer le règlement").setMessage(method+" — "+eur(total())).setNegativeButton("Annuler",null).setPositiveButton("Valider",(d,w)->{HashMap<String,Double> used=new HashMap<>();for(JSONObject c:cart){String code=c.optString("code");if(!code.isEmpty())used.put(code,used.getOrDefault(code,0.0)+c.optDouble("qty"));}for(String code:used.keySet()){JSONObject p=find(code);if(p==null||p.optDouble("stock")<used.get(code)){toast("Stock insuffisant : "+code);return;}}for(String code:used.keySet()){JSONObject p=find(code);try{p.put("stock",p.optDouble("stock")-used.get(code));}catch(Exception e){}}JSONObject s=new JSONObject();try{s.put("time",System.currentTimeMillis());s.put("method",method);s.put("total",total());s.put("items",new JSONArray(cart));}catch(Exception e){}sales.add(s);cart.clear();save();show("Caisse");toast("Vente enregistrée");}).show();}
 void rapports(){LinearLayout p=panel(body,"Rapports et historique");double sum=0;for(JSONObject s:sales)sum+=s.optDouble("total");p.addView(txt("Nombre de ventes : "+sales.size()+"\nChiffre d'affaires : "+eur(sum)+"\nProduits : "+products.size(),23,navy));for(JSONObject s:sales)p.addView(txt(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.FRANCE).format(new Date(s.optLong("time")))+" — "+s.optString("method")+" — "+eur(s.optDouble("total")),16,navy));}
 void parametres(){LinearLayout p=panel(body,"Paramètres");p.addView(txt("Les articles et les ventes sont sauvegardés localement hors ligne. Les moyens de paiement sont enregistrés, sans encaissement bancaire électronique.",19,navy));}
}
