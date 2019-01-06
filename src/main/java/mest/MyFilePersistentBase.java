package mest;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFilePersistentBase {

    protected String path;
    public static String PATH_SEPERATOR = "/";

    static {//根据系统获取分隔符
        String property = System.getProperties().getProperty("file.separator");
        if (property != null)
            PATH_SEPERATOR = property;
    }
    /**
     * @param path
     */
    public void setPath(String path) {
        if (!path.endsWith(PATH_SEPERATOR))
            path = (new StringBuilder()).append(path).append(PATH_SEPERATOR)
                    .toString();
        this.path = path;
    }
    /**
     * 创建文件
     * @param fullName
     * @return
     */
    public File getFile(String fullName) {
        checkAndMakeParentDirecotry(fullName);
        return new File(fullName);
    }
    /**
     * 创建路径
     * 检查改文件所在路径是否存在
     * @param fullName
     */
    public void checkAndMakeParentDirecotry(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if (index > 0) {
            String path = fullName.substring(0, index);
            File file = new File(path);
            if (!file.exists())
                file.mkdirs();
        }
    }

    public String getPath() {
        return path;
    }
    private String filename="";
    private DateFormat bf=null;
    private Date date=null;
    /**
     * 日期格式化，作为文件名
     * @return
     */
    public String filenameByDate() {
        bf = new SimpleDateFormat("yyyy-MM-dd");
        date=new Date();
        filename=bf.format(date).replaceAll("-", "");
        return filename;
    }
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        String test=new MyFilePersistentBase().filenameByDate();
        System.out.println(test);
    }

}
