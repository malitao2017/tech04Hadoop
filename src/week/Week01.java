package week;

public class Week01 {
	
	public static void main(String[] args) {
		Week1_1.compute(0,1f, 1f, 1f, 1f);
		System.out.println("�ڶ���------------------");
		Week1_2.compute(0,1f, 1f, 1f, 1f);
	}
}
/**
 * ���ݻõ�Ƭ�е�9ҳ�������ġ�4��ҳģ�͡�  
 * 1������ÿ����ҳ��pagerank�����㡢��̼�����������ݷ������ߣ�����R���ȸ��ַ������ɣ�������ץͼ�����ļ�����̺ͽ��
 */
class Week1_1{
	public static void compute(int count,double q1,double q2,double q3,double q4){
		double a1,a2,a3,a4;
		a1=0f;
		a2=1f/3f*q1+q4;
		a3=1f/3f*q1+1f/2f*q2;
		a4=1f/3f*q1+1f/2f*q2+q3;
		count++;
		System.out.println("pagerank��"+count+"��  "+a1+" "+a2+" "+a3+" "+a4);
		
		if(a2-q2>0.000001||q2-a2>0.000001){
			
		}else{
			return;
		}
		compute(count,a1, a2, a3, a4);
	}
}

/**
 * 2������map-reduce��˼�룬���ڼ���������ڵ�A��B������㣬������ҳ1��2������A��
 * ��ҳ3��4������B������������pagerank�������  
 * ��ʹ��java����ģ����2���ڵ��map����һ������reduce
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
		System.out.println("��ڵ㣺��"+count+"��  "+retReduce[0]+" "+retReduce[1]+" "+retReduce[2]+" "+retReduce[3]);
		if(retReduce[1]-q2>0.000001||q2-retReduce[1]>0.000001){
			
		}else{
			return;
		}
		compute(count,retReduce[0], retReduce[1], retReduce[2], retReduce[3]);
	}
}
