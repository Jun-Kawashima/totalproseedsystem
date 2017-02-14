package jp.co.iccom.kawashima_jun.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Calcsale {
	public static void main(String[] args) {
		HashMap<String,String> branch = new HashMap<String,String>();
		HashMap<String,Long> branchsale = new HashMap<String,Long>();
		HashMap<String,String> commodity = new HashMap<String,String>();
		HashMap<String,Long> commoditysale = new HashMap<String,Long>();

		if(!readfile(args[0] , "branch.lst" , "[0-9]{3}" , "支店" , branch,branchsale))
			return;
		if(!readfile(args[0] , "commodity.lst" , "[a-zA-Z0-9]{8}$" , "商品" , commodity,commoditysale))
			return;

		//売上ファイル連番判定
		HashMap<String,String> proseed = new HashMap<String,String>();
		ArrayList<String> filelist = new ArrayList<String>();
		try {
			File dir = new File(args[0]);//ディレクトリ
			File[] files  = dir.listFiles();

			for(int i = 0;i < files.length; i++) {
				File file = files[i];  //ここまでだとファイルが格納されているルートがでる
				String filename = file.getName();  //ここでFileの要素をString化する(ファイル以前のルートが消える)
				if(filename.matches("([0-9]{8}).rcd$")){//数字8桁且つrcdファイルの判定(それ以外は候補から外れる)
					filelist.add(filename);//filelistに加える
					//System.out.println(filename);
				}
			}
			for(int i = 0;i < filelist.size()-1; i++) {//filelistの要素を存在する分だけ羅列
				String str = filelist.get(i).substring(0,8);//数字8桁
				String stl = filelist.get(i + 1).substring(0,8);
				int a = Integer.parseInt(str);//参照a
				int b = Integer.parseInt(stl);//参照b
				if( b - a != 1) {//もし、b-aが1でない＝ファイル間の空き
					 System.out.println("売上ファイル名が連番になっていません");
					 return;
				}
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch(IndexOutOfBoundsException e) {
			System.out.println("予期せぬエラーが発生しました1");
			return;
		}
		//売上ファイルの中身の判定
		HashMap<String,String> earnings = new HashMap<String,String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			for(int i = 0;i < filelist.size(); i++) {//売上ファイルのリストを存在する分羅列
				ArrayList<String> salefile = new ArrayList<String>();
				File file = new File(args[0],filelist.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				while((str = br.readLine()) != null) {
					salefile.add(str);
				}
				if(salefile.size() != 3){//売上ファイル内容が3行ではない(フォーマットの判定)
					System.out.println(file.getName()+"のフォーマットが不正です");
					return;
				}
				String branchcode = salefile.get(0);//支店コード
				if(!branch.containsKey(branchcode)){//
					System.out.println(file.getName()+"の支店コードが不正です");
					return;
				}
				String commoditycode = salefile.get(1);//商品コード
				if(!commodity.containsKey(commoditycode)) {
					System.out.println(file.getName()+"の商品コードが不正です");
					return;
				}
				String price = salefile.get(2);
				long money = Long.parseLong(price);
				long branchsum = branchsale.get(branchcode);
				long commoditysum = commoditysale.get(commoditycode);
				branchsum += money;
				if(branchsum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				commoditysum += money;
				if(commoditysum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				branchsale.put(branchcode,branchsum);
				commoditysale.put(commoditycode, commoditysum);
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");

			return;
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		if(!output(args[0],"branch.out",branchsale,branch))
			return;
		if(!output(args[0],"commodity.out",commoditysale,commodity))
			return;
	}
	public static boolean readfile(String root , String fileName , String format , String name , HashMap<String,String> branchMap,HashMap<String,Long> branchsaleMap){
		BufferedReader br = null;
		try {//支店定義ファイル
			File file = new File(root,fileName);//ファイル読み込み
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String s;

			while((s = br.readLine()) != null) {//フォーマット判定
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches(format)) {
					System.out.println(name+"定義ファイルのフォーマットが不正です");
					return false;
				}
				branchMap.put(items[0], items[1]);//Map置く
				branchsaleMap.put(items[0], 0L);
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
		} catch(FileNotFoundException e) {
			System.out.println(name+"定義ファイルが存在しません");
			return false;
		} catch(IOException e) {
			 System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally{
			if(br !=null){
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		}
		return true;
	}
	public static boolean output(String root , String fileName , HashMap<String,Long> branchMap ,HashMap<String,String> branchsaleMap){
		BufferedWriter bw = null;
		try {
			File writefile = new File(root,fileName);
			FileWriter fw = new FileWriter(writefile);
			bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>> (branchMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

				public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s: entries){
				bw.write(s.getKey() + "," + branchsaleMap.get(s.getKey()) + "," +s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally{
			if(bw !=null){
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		}
		return true;
	}
}