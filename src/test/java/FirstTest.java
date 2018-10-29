
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utl.ExcelReader;
import utl.JsonFormatTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FirstTest {

    private HttpClient httpClient = new DefaultHttpClient();
    private HttpPost httppost;
    private HttpResponse response;
    private HttpEntity entity;
    private String postResult;
    private JSONObject jsonResult;

    @DataProvider(name = "LoginData")
    public Object[][] login() {
        // 测试数据准备
        String file = "." + File.separator + "TestData" + File.separator + "LoginTest.xls";
        Object[][] records ;
        records = ExcelReader.getExpectationData(file, "login");
        return records ;
    }

    @Test(dataProvider = "LoginData")
    public void loginJDTest(String caseDescription,String loginURL, String uuid,String eid,String fp,String _t,
                            String loginType,String loginname,String nloginpwd, String chkRememberMe,
                            String authcode,String pubKey,String sa_token,String seqSid) {

        System.out.println("=====" + caseDescription + "=====");
        //创建一个httppost请求
        httppost = new HttpPost(loginURL);

        //创建Post请求参数
        List<NameValuePair> formparams1 = new ArrayList<NameValuePair>();
        formparams1.add(new BasicNameValuePair("uuid",uuid));
        formparams1.add(new BasicNameValuePair("eid",eid));
        formparams1.add(new BasicNameValuePair("fp",fp));
        formparams1.add(new BasicNameValuePair("_t",_t));
        formparams1.add(new BasicNameValuePair("loginType",loginType));
        formparams1.add(new BasicNameValuePair("loginname",loginname));
        formparams1.add(new BasicNameValuePair("nloginpwd",nloginpwd));
        formparams1.add(new BasicNameValuePair("chkRememberMe",chkRememberMe));
        formparams1.add(new BasicNameValuePair("authcode",authcode));
        formparams1.add(new BasicNameValuePair("pubKey",pubKey));
        formparams1.add(new BasicNameValuePair("sa_token",sa_token));
        formparams1.add(new BasicNameValuePair("seqSid",seqSid));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(formparams1,"UTF-8"));
            response = httpClient.execute(httppost);
            entity = response.getEntity();
            // 在这里可以用Jsoup之类的工具对返回结果进行分析，以判断创建是否成功
            postResult = EntityUtils.toString(entity, "UTF-8");

            //System.out.println("查看登录接口请求返回的结果：" + postResult);

            //由于京东这个接口返回的请求头尾是“（）”，所以需要去掉，才可以转化成Json格式
            if (postResult.startsWith("(")){
                postResult = postResult.substring(1,postResult.length());
            }
            if (postResult.endsWith(")")){
                postResult = postResult.substring(0,postResult.length()-1);
            }
            //转换成json格式
            jsonResult = JSONObject.fromObject(postResult); //该结果可用来提取任何想要的值
            //Assert.assertEquals(jsonResult.getString("_t"),"_ntiyYur"); //assert验证结果，注意这个验证结果会失败,
                                                                          // 因为京东对登录结果做了限制，不让我们这样做接口测试，但是我们可以当做例子
            JsonFormatTool tool = new JsonFormatTool();
            String Result = tool.formatJson(jsonResult.toString()); //自定义控制台打印结果的格式
            System.out.println("请求后返回的结果：" + Result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        httppost.releaseConnection();
    }
}
