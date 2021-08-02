package njgis.opengms.portal.test.queue.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.test.queue.controller.ForestInvoke;
import njgis.opengms.portal.test.queue.dao.RunTaskDao;
import njgis.opengms.portal.test.queue.dao.RunningDao;
import njgis.opengms.portal.test.queue.dao.SubmitedTaskDao;
import njgis.opengms.portal.test.queue.entity.DataItem;
import njgis.opengms.portal.test.queue.entity.IOData;
import njgis.opengms.portal.test.queue.entity.RunTask;
import njgis.opengms.portal.test.queue.entity.SubmitedTask;
import njgis.opengms.portal.utils.MyHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @Description description
 * @Author bin
 * @Date 2021/07/16
 */
@Service
public class RunService {

    // 注入forest
    @Autowired
    private ForestInvoke forestInvoke;

    @Autowired
    private SubmitedTaskDao submitedTaskDao;

    @Autowired
    private ServerService serverListener;

    @Autowired
    private RunningDao runningDao;

   @Autowired
    private RunTaskDao runTaskDao;

    @Resource
    private MongoTemplate mongoTemplate;

    // 调用模型服务容器接口
    public void run(RunTask runTask) throws IOException {
        // TODO 做try-catch处理，可能请求模型容器接口出错
        String url = runTask.getIp() + ":" + runTask.getPort() + "/modelser";
        IOData inputData = new IOData();
        inputData.setInputdata(runTask.getInputData());
        String jsonString = JSON.toJSONString(inputData);

        // 使用MyHttpUtils请求
        // JSONObject param = JSONObject.parseObject(jsonString);
        // String result = MyHttpUtils.POSTWithJSON(url,"UTF-8",null,param);

        // 使用forest请求
        Map msMap = forestInvoke.invokeRunMs(url,jsonString);
        String msrid = (String)msMap.get("data");
        System.out.println("[          runTaskId:msrid] -- " + runTask.getTaskId() + ":" + msrid);
        // 存储模型运行信息到运行表中
        runTask.setMsrid(msrid);
        runTask.setStatus(1);
        runningDao.save(runTask);

    }

    // 从模型服务容器中检查任务信息
    public RunTask updateTaskFromMSC(String runTaskID) throws IOException, URISyntaxException {

        RunTask runTask = runTaskDao.findFirstByTaskId(runTaskID);

        if(runTask.getStatus()==1){//只有状态更改为正在运行的才需要去模型容器更新状态
            String ip = runTask.getIp();
            String port = runTask.getPort();
            String msrid = runTask.getMsrid();

            String url = "http://" + ip + ":" +"port" + "/modelserrun/json/" + msrid;
            String j_result = MyHttpUtils.GET(url,"UTF-8", null);

            JSONObject result = JSONObject.parseObject(j_result);

            int code = result.getInteger("code");

            if(code==-1){
//            runTask.setStatus(-1);
            }else if(code==1){
                JSONObject modelInfo = (JSONObject) result.getJSONObject("data");
                if(modelInfo!=null){
                    int status = modelInfo.getInteger("msr_status");
                    runTask.setStatus(status);
                    if(status==1){

                        JSONArray outputArray = modelInfo.getJSONArray("msr_output");
                        List<DataItem> outputs = JSONObject.parseArray(outputArray.toString(),DataItem.class);
                        runTask.setOutputData(outputs);

                        //更新数据库单字段
                        Query query = Query.query(Criteria.where("taskId").is(runTask.getTaskId()));

                        Update update = new Update();
                        update.set("outputs",outputs);
                        update.set("status",status);
                        mongoTemplate.updateFirst(query, update, RunTask.class);

                        //更新关联task字段
                        List<String> relatedTasksIdList = runTask.getRelatedTasks();

                        for(int i=0;i<relatedTasksIdList.size();i++){
                            String relatedTaskId = relatedTasksIdList.get(i);

                            SubmitedTask submitedTask = submitedTaskDao.findByTaskId(relatedTaskId);

                            submitedTask.setStatus(2);
                            submitedTask.setOutputData(outputs);

                            submitedTaskDao.save(submitedTask);
                        }

                    }else if(status == -1){
                        //更新数据库单字段
                        Query query = Query.query(Criteria.where("taskId").is(runTask.getTaskId()));

                        Update update = new Update();
                        update.set("status",status);
                        mongoTemplate.updateFirst(query, update, RunTask.class);

                        //更新关联task字段
                        List<String> relatedTasksIdList = runTask.getRelatedTasks();

                        for(int i=0;i<relatedTasksIdList.size();i++){
                            String relatedTaskId = relatedTasksIdList.get(i);

                            SubmitedTask submitedTask = submitedTaskDao.findByTaskId(relatedTaskId);

                            submitedTask.setStatus(2);

                            submitedTaskDao.save(submitedTask);
                        }
                    }

                }


            }
        }


        return runTask;
    }

    // 更新正在运行的模型任务
    public void runningListener(){
        // JSONObject msrInfo;
        // List<RunTask> RunTasks = runningDao.findAll();
        // for (RunTask RunTask : RunTasks) {
        //     TaskTable execTask = taskDao.findByTaskId(RunTask.getTaskId());
        //     if (execTask.getStatus() == 1){
        //         String url = execTask.getIp() + ":" + execTask.getPort();
        //         msrInfo = (JSONObject) invoke.getMsrInfo(url,RunTask.getMsrid()).get("data");
        //         int msrStatus = (int) msrInfo.get("msr_status");
        //         if (msrStatus == 0)
        //             break;
        //         if (msrStatus == 1){
        //             // JSONObject转化为List
        //             List<DataItem> outputdata = JSONObject.parseArray(msrInfo.get("msr_output").toString(),DataItem.class) ;
        //             execTask.setOutputData(outputdata);
        //             execTask.setStatus(2);
        //             // 任务更新
        //             taskDao.save(execTask);
        //         }
        //         else if (msrStatus == -1){
        //             execTask.setStatus(-1);
        //             taskDao.save(execTask);
        //         }
        //
        //         // 任务结束,更新服务器信息
        //         serverListener.updateServerStatus(execTask.getIp());
        //         System.out.println("[          Task Finished] -- taskId : " + execTask.getTaskId());
        //     }
        // }


    }

}