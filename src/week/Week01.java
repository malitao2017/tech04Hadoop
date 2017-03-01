package week;

public class Week01 {
	
	public static void main(String[] args) {
		Week1_1.compute(0,1f, 1f, 1f, 1f);
		System.out.println("第二个------------------");
		Week1_2.compute(0,1f, 1f, 1f, 1f);
	}
}
/**
 * 根据幻灯片中第9页所给出的“4网页模型”  
 * 1）计算每个网页的pagerank，手算、编程计算或利用数据分析工具（例如R）等各种方法均可，拷贝或抓图完整的计算过程和结果
 */
class Week1_1{
	public static void compute(int count,double q1,double q2,double q3,double q4){
		double a1,a2,a3,a4;
		a1=0f;
		a2=1f/3f*q1+q4;
		a3=1f/3f*q1+1f/2f*q2;
		a4=1f/3f*q1+1f/2f*q2+q3;
		count++;
		System.out.println("pagerank第"+count+"次  "+a1+" "+a2+" "+a3+" "+a4);
		
		if(a2-q2>0.000001||q2-a2>0.000001){
			
		}else{
			return;
		}
		compute(count,a1, a2, a3, a4);
	}
}

/**
 * 2）按照map-reduce的思想，现在假设有物理节点A，B参与计算，其中网页1、2保存于A，
 * 网页3、4保存于B，试述完整的pagerank计算过程  
 * 我使用java语言模拟了2个节点的map，和一个结点的reduce
 */

class Week1_2{
	public static double[] map1(double q1,double q2){
		double[] ret = new double[4];
		ret[0]=0f*q1+0f*q2;
		ret[1]=1f/3f*q1+0f*q2;
		ret[2]=1f/3f*q1+1f/2f*q2;
		ret[3]=1f/3f*q1+1f/2f*q2;
		return ret;
	}
	public static double[] map2(double q3,double q4){
		double[] ret = new double[4];
		ret[0]=0f*q3+0f*q4;
		ret[1]=0f*q3+1f*q4;
		ret[2]=0f*q3+0f*q4;
		ret[3]=1f*q3+0f*q4;
		return ret;
	}
	public static double[] reduce(double[] retMap1,double[] retMap2){
		double[] ret = new double[4];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = retMap1[i]+retMap2[i];
		}
		return ret;
	}
	public static void compute(int count,double q1,double q2,double q3,double q4){
		double[] retMap1 = map1(q1, q2);
		double[] retMap2 = map2(q3, q4);
		double[] retReduce =reduce(retMap1, retMap2);
		count++;
		System.out.println("多节点：第"+count+"次  "+retReduce[0]+" "+retReduce[1]+" "+retReduce[2]+" "+retReduce[3]);
		if(retReduce[1]-q2>0.000001||q2-retReduce[1]>0.000001){
			
		}else{
			return;
		}
		compute(count,retReduce[0], retReduce[1], retReduce[2], retReduce[3]);
	}
}
