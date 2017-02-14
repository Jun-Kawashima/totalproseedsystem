package jp.co.iccom.kawashima_jun.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
		HashMap<String,Long> branchSale = new HashMap<String,Long>();
		HashMap<String,String> commodity = new HashMap<String,String>();
		HashMap<String,Long> commoditySale = new HashMap<String,Long>();

		if(!readfile(args[0], "branch.lst", "[0-9]{3}", "支店", branch, branchSale))
			return;
		if(!readfile(args[0], "commodity.lst", "[a-zA-Z0-9]{8}$", "商品", commodity, commoditySale))
			return;

		//売上ファイル連番判定
		HashMap<String,String> proseed = new HashMap<String, String>();
		ArrayList<String> fileList = new ArrayList<String>();
		try {
			File dir = new File(args[0]);//ディレクトリ
			File[] files  = dir.listFiles();

			for(int i = 0; i < files.length; i++) {
				File file = files[i];  //ここまでだとファイルが格納されているルートがでる
				String fileName = file.getName();  //ここでFileの要素をString化する(ファイル以前のルートが消える)
				if(fileName.matches("([0-9]{8}).rcd$")){//数字8桁且つrcdファイルの判定(それ以外は候補から外れる)
					fileList.add(fileName);//filelistに加える
				}
			}
			for(int i = 0; i < fileList.size()-1; i++) {//filelistの要素を存在する分だけ羅列
				String str = fileList.get(i).substring(0, 8);//数字8桁
				String stl = fileList.get(i + 1).substring(0, 8);
				int back = Integer.parseInt(str);//参照a
				int front = Integer.parseInt(stl);//参照b
				if( back - front != 1) {//もし、b-aが1でない＝ファイル間の空き
					 System.out.println("売上ファイル名が連番になっていません");
					 return;
				}
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//売上ファイルの中身の判定
		HashMap<String, String> earnings = new HashMap<String, String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			for(int i = 0; i < fileList.size(); i++) {//売上ファイルのリストを存在する分羅列
				ArrayList<String> saleFile = new ArrayList<String>();
				File file = new File(args[0], fileList.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				while((str = br.readLine()) != null) {
					saleFile.add(str);
				}
				if(saleFile.size() != 3){//売上ファイル内容が3行ではない(フォーマットの判定)
					System.out.println(file.getName()+"のフォーマットが不正です");
					return;
				}
				String branchCode = saleFile.get(0);//支店コード
				if(!branch.containsKey(branchCode)){//
					System.out.println(file.getName()+ "の支店コードが不正です");
					return;
				}
				String commodityCode = saleFile.get(1);//商品コード
				if(!commodity.containsKey(commodityCode)) {
					System.out.println(file.getName()+ "の商品コードが不正です");
					return;
				}
				String price = saleFile.get(2);
				long money = Long.parseLong(price);
				long branchSum = branchSale.get(branchCode);
				long commoditySum = commoditySale.get(commodityCode);
				branchSum += money;
				if(branchSum > 9999999999L) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				commoditySum += money;
				if(commoditySum > 9999999999L) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				branchSale.put(branchCode, branchSum);
				commoditySale.put(commodityCode, commoditySum);
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		if(!output(args[0], "branch.out", branchSale,branch))
			return;
		if(!output(args[0], "commodity.out", commoditySale, commodity))
			return;
	}
	public static boolean readfile(String root, String fileName, String format, String name, HashMap<String, String> branchMap, HashMap<String, Long> branchsaleMap){
		BufferedReader br = null;
		try {//支店定義ファイル
			File file = new File(root,fileName);//ファイル読み込み
			if(!file.exists()){
				System.out.println("予期せぬエラーが発生しました");
			} else {
				FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null) {//フォーマット判定
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches(format)) {
					System.out.println(name+ "定義ファイルのフォーマットが不正です");
					return false;
				}
				branchMap.put(items[0], items[1]);//Map置く
				branchsaleMap.put(items[0], 0L);
			}
			}
		} catch(IOException e) {
			 System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			if(br != null){
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
	public static boolean output(String root, String fileName, HashMap<String,Long> branchMap ,HashMap<String,String> branchSaleMap){
		BufferedWriter bw = null;
		try {
			File writeFile = new File(root,fileName);
			FileWriter fw = new FileWriter(writeFile);
			bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>> (branchMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

				public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s: entries){
				bw.write(s.getKey() + "," + branchSaleMap.get(s.getKey()) + "," +s.getValue());
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