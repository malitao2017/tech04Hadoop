package baseKnowledge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Know {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * short �����������
		 * float��double����ʹ�ã�ֻ���ڿ�ѧ�͹��̼��㣬�ڼ�������м����׶�ʧ����
		 * byte/short/char ->int->long->float->double
			byte/short/char֮�䲻����ת��������������ʱ����ת��Ϊint

		 */
//		short s1=1;s1=s1+1; //������� s1+1��������int
		short s2=1;s2+=1; //System.out.println(s2);
		byte b= 1; b=(byte) (b+2);
		
		/**
		 * �������뷽ʽ
		 * ��������1/2֮������float
		 */
//		System.out.println(Math.round(11.5));//12
//		System.out.println(Math.round(-11.5));//-11


		/**
		 * List Set�̳�Collection
		 * Map��
		 */
		List<String> a = null;
		Set<String> bb=null;
		Map<String, String> cc=null;
		
		/**
		 * 4���̣߳�����ÿ�μ�1������ÿ�μ�1
		 * �ڲ��࣬û����˳����
		 */
		testThread();
		
		
	}
	/**
	 * �߳����
	 * @return 
	 */
	static void testThread(){
		Know kn = new Know();
		inc inc=kn.new inc();
		dec dec=kn.new dec();
		for(int i=0;i<2;i++){
			Thread td = new Thread(inc);
			td.start();
			td = new Thread(dec);
			td.start();
		}
	}
	private int j ;
	private synchronized void inc(){
		j++;
		System.out.println(Thread.currentThread().getName()+"-inc:"+j);
	}
	private synchronized void dec(){
		j--;
		System.out.println(Thread.currentThread().getName()+"-dec:"+j);
	}
	class inc implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<100;i++){
				inc();
			}
		}
	}
	class dec implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<100;i++){
				dec();
			}
		}
	}
	
}


