package mest;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoByWebMagic implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static int count =0;

    @Override
    public void process(Page page) {
        //电影详情链接movieLink的正则表达式
        String movieLinkReg="/html/gndy/\\w{4}/\\d{8}/\\d{5}.html";
        Pattern movieLinkPattern=Pattern.compile(movieLinkReg);
        //写相应的xpath
        String movieNameXpath="//title/text()";
        //String movieDownloadXpath="//div[starts-with(@href,'ftp')]/text()";
        String movieDownloadXpath="//div[@id='Zoom']/span/p/img/@src";
        String movieLinkXpath="//div[@class='co_content2']/ul/a[@href]";
        List<String> movieLinkList=new ArrayList<String>();
        //结果抽取
        Selectable moviePage;
        Selectable movieNameS;
        Selectable movieDownloadS;
        if("http://www.dytt8.net".equals(page.getUrl().toString())){
            //抽取结果
            moviePage=page.getHtml().xpath(movieLinkXpath);
            //选中结果
            movieLinkList=moviePage.all();
            //循环遍历
            String movieLink="";
            Matcher movieLinkMatcher;
            for(int i=1;i<movieLinkList.size();i++){
                //第一条过滤，从第二条开始遍历
                movieLink = movieLinkList.get(i);
                //正则匹配
                movieLinkMatcher=movieLinkPattern.matcher(movieLink);
                if(movieLinkMatcher.find()){//匹配子串
                    movieLink=movieLinkMatcher.group();//返回匹配到的字串
                    //将找到的链接放到ddTargetRequest里面，会自动发起请求
                    page.addTargetRequest(movieLink);
                    //输出到控制台
                    System.out.println(movieLink);
                }
            }
        }else{//第二次请求，电影详情页面
            //获取html
            movieNameS=page.getHtml().xpath(movieNameXpath);
            movieDownloadS=page.getHtml().xpath(movieDownloadXpath);
            page.putField("movieName",movieNameS);
            //page.putField("downloadURL", page.getHtml().xpath("//a[starts-with(@href,'ftp')]/text()").toString());
            page.putField("downloadURL", movieDownloadS);
            count++;
        }
        movieLinkList.clear();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("开始爬取...");
        String filepath = new MyJsonFilePipeline("D:\\webMagic2\\").getPath();
        File file = new File(filepath);
        if(file.exists()){
            file.delete();
        }
        startTime = System.currentTimeMillis();
        Spider.create(new InfoByWebMagic())
                .setDownloader(new HttpClientDownloader())
                .addUrl("http://www.douyu.com")
                .addPipeline(new ConsolePipeline())
                .addPipeline(new MyJsonFilePipeline("D:\\webMagic2\\"))
                .thread(5)
                .run();
        endTime = System.currentTimeMillis();
        System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒，抓取了"+count+"条记录");
    }

}