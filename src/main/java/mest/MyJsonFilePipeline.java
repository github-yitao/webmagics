package mest;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MyJsonFilePipeline extends MyFilePersistentBase implements Pipeline {
    private Logger logger;
    /**
     * 空参构造--使用默认路径
     */
    public MyJsonFilePipeline(){
        logger = LoggerFactory.getLogger(getClass());
        setPath("/data/webmagic");
    }
    /**
     * 参数构造--指定路径
     * @param path
     */
    public MyJsonFilePipeline(String path) {
        logger = LoggerFactory.getLogger(getClass());
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        try{
            String filepath = getPath();
            //true---不将原先文件内容覆盖
            PrintWriter printWriter = new PrintWriter(new FileWriter(
                    getFile(filepath),true));
            if(resultItems.getAll().size()>0) {
                printWriter.write(JSON.toJSONString(resultItems.getAll()) + "\r\n");
                printWriter.close();
               /* Object obj = resultItems.getAll().get("movieName");
                Object obj1 = resultItems.getAll().get("downloadURL");
                String urlString = obj1.toString();
                String suffix = getSuffix(urlString);
                String filename = obj.toString();
                try {
                    if(suffix!=null&&!"".equals(suffix)) {
                        download(urlString, filename+suffix);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        } catch (IOException e) {
            logger.warn("write file error", e);
            //System.out.println("文件写入出异常！！！！");
        }
    }

    public String  getSuffix(String url){
        int i = url.lastIndexOf(".");
        String suffix = "";
        if(i != -1){
            suffix = url.substring(i,url.length());
        }
        return suffix;
    }

    /**
     * 下载文件到本地
     *
     * @param urlString
     *          被下载的文件地址
     * @param filename
     *          本地文件名
     * @throws Exception
     *           各种异常
     */
    public void download(String urlString,String filename) throws Exception {

        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        OutputStream os = new FileOutputStream("D:\\webMagic2\\"+filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    public String getPath(){
        String filepath=new StringBuilder().append(this.path).
                append(PATH_SEPERATOR).
                append(filenameByDate()).append(".txt").toString();
        return filepath;
    }

}
