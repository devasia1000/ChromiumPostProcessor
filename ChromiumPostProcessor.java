
import java.io.*;

public class ChromiumPostProcessor {
    
    //These variables are user-editable
    static File chromium_log=new File("/home/devasia/Desktop/chromium.txt");
    static File output_file=new File("/home/devasia/Desktop/chromium_report.txt");
    static int stallThreshold=60; // in milliseconds
    static int linkSpeed=10000; //in kbps
    
    //These variables are NOT user editable
    static BufferedWriter wt;
    static double sum=0;
    static int frame_count=0;
    

    public static void main(String args[]) {
        try{
        
        BufferedReader rd =new BufferedReader(new FileReader(chromium_log));
        wt=new BufferedWriter(new FileWriter(output_file));
        
        String line=null, log="";
        while((line=rd.readLine())!=null){
            if(line.startsWith("#")){
                log=log+line+"\n";
            }
        }
        
        String arr[]=log.split("\n");
        
        for(int i=0;i<arr.length-2;i++){
            processLine(arr[i], arr[i+1]);       
        }
        
        double avg=sum/arr.length;
        log("Average delay between frames: "+avg);
        
        wt.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void log(String message) throws Exception{
        System.out.println(message);
        wt.write(message+"\n");
        wt.flush();
    }
    
    public static void processLine(String line1, String line2) throws Exception{
        if(line1.contains("#Loading at ") && line2.contains("#FrameReady at ")){
            line1=line1.replace("#Loading at ", "");
            double d1=Double.parseDouble(line1);
            line2=line2.replace("#FrameReady at ", "");
            double d2=Double.parseDouble(line2);
            double t=d2-d1;
            int time=(int) t;
            log("Loading Time: "+time+"ms");
        }
        
        else if(line1.contains("#FrameReady at ") && line2.contains("#FrameReady at ")){
            frame_count++;
            
            line1=line1.replace("#FrameReady at ", "");
            double d1=Double.parseDouble(line1);
            line2=line2.replace("#FrameReady at ", "");
            double d2=Double.parseDouble(line2);
            double t=d2-d1;
            int time=(int) t;
            
            sum=sum+time;
            
            if(time>stallThreshold){
                log("Stall of "+time+"ms detected between Frame "+frame_count+" and Frame "+(frame_count+1));
            }
            else if(time<20){
                log("Quicken of "+time+"ms detected between Frame "+frame_count+" and Frame "+(frame_count+1));
            }
        }
        
        else if(line1.contains("#Seek at ") && line2.contains("#FrameReady at ")){
            line1=line1.replace("#Seek at ", "");
            double d1=Double.parseDouble(line1);
            line2=line2.replace("#FrameReady at ", "");
            double d2=Double.parseDouble(line2);
            double t=d2-d1;
            int time=(int) t;
            log("Seek time of "+time+"ms between Frame "+frame_count+" and Frame "+(frame_count+1));
        }
    }
}
