package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.ComputableModelDao;
import njgis.opengms.portal.dao.ModelItemDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.Localization;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.data.SimpleFileInfo;
import njgis.opengms.portal.entity.doo.model.ModelItemRelate;
import njgis.opengms.portal.entity.dto.model.ComputableModelResultDTO;
import njgis.opengms.portal.entity.po.ComputableModel;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.IOUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static njgis.opengms.portal.utils.ModelServiceUtils.checkMdlJson;
import static njgis.opengms.portal.utils.Utils.saveFiles;

/**
 * @Description Computable Model Service
 * @Author kx
 * @Date 21/10/12
 * @Version 1.0.0
 */
@Service
public class ComputableModelService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    GenericService genericService;

    @Autowired
    ComputableModelDao computableModelDao;

    @Autowired
    ModelItemDao modelItemDao;

    @Value("${htmlLoadPath}")
    String htmlLoadPath;

    @Value("${resourcePath}")
    String resourcePath;

    /**
     * @Description 计算模型详情页面
     * @param id
     * @Return org.springframework.web.servlet.ModelAndView
     * @Author kx
     * @Date 21/11/11
     **/
    public ModelAndView getPage(String id) {
        //条目信息
        try {

            ModelAndView modelAndView = new ModelAndView();

            ComputableModel computableModel = computableModelDao.findFirstById(id);

            computableModel=(ComputableModel)genericService.recordViewCount(computableModel);
            computableModelDao.save(computableModel);

            //时间
            Date date = computableModel.getCreateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateResult = simpleDateFormat.format(date);

            //用户信息
            JSONObject userJson = userService.getItemUserInfoByEmail(computableModel.getAuthor());
            //资源信息
            JSONArray resourceArray = new JSONArray();
            List<String> resources = computableModel.getResources();

            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {

                    String path = resources.get(i);

                    String[] arr = path.split("\\.");
                    String suffix = arr[arr.length - 1];

                    arr = path.split("/");
                    String name = arr[arr.length - 1].substring(14);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("suffix", suffix);
                    jsonObject.put("path", resources.get(i));
                    resourceArray.add(jsonObject);

                }

            }

            String lastModifyTime = simpleDateFormat.format(computableModel.getLastModifyTime());

            //修改者信息
            String lastModifier = computableModel.getLastModifier();
            JSONObject modifierJson = null;
            if (lastModifier != null) {
                modifierJson = userService.getItemUserInfoByEmail(lastModifier);
            }

            //authorship
            String authorshipString="";
            List<AuthorInfo> authorshipList=computableModel.getAuthorships();
            if(authorshipList!=null){
                for (AuthorInfo author:authorshipList
                        ) {
                    if(authorshipString.equals("")){
                        authorshipString+=author.getName();
                    }
                    else{
                        authorshipString+=", "+author.getName();
                    }

                }
            }

            //relateModelItemList
            List<String> modelItemIdList = computableModel.getRelatedModelItems();
            JSONArray modelItemInfoList = ArrayUtils.parseListToJSONArray(genericService.getRelatedModelInfoList(modelItemIdList));

            modelAndView.setViewName("computable_model");

            modelAndView.addObject("modelInfo", computableModel);
//            modelAndView.addObject("classifications", classResult);
            modelAndView.addObject("date", dateResult);
            modelAndView.addObject("year", calendar.get(Calendar.YEAR));
            modelAndView.addObject("user", userJson);
            modelAndView.addObject("authorship", authorshipString);
            modelAndView.addObject("resources", resourceArray);
            if(computableModel.getMdl()!=null) {
                modelAndView.addObject("mdlJson", ModelServiceUtils.convertMdl(computableModel.getMdl()).getJSONObject("mdl"));
            }
            JSONObject mdlJson = (JSONObject) JSONObject.toJSON(computableModel.getMdlJson());
            if (mdlJson != null) {
                JSONObject modelClass = (JSONObject) mdlJson.getJSONArray("ModelClass").get(0);
                JSONObject behavior = (JSONObject) modelClass.getJSONArray("Behavior").get(0);
                modelAndView.addObject("behavior", behavior);
            }
            modelAndView.addObject("loadPath", htmlLoadPath);
            modelAndView.addObject("lastModifier", modifierJson);
            modelAndView.addObject("lastModifyTime", lastModifyTime);
//            modelAndView.addObject("relateModelItem", modelItemInfo);
            modelAndView.addObject("relateModelItemList",modelItemInfoList);//之前只关联一个modelitem,现在改为多个
            return modelAndView;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new MyException(e.getMessage());
        }

    }

    public ComputableModel getInfo(String id){
        ComputableModel computableModel=computableModelDao.findFirstById(id);
        ComputableModelResultDTO computableModelResultDTO=new ComputableModelResultDTO();

        BeanUtils.copyProperties(computableModel,computableModelResultDTO);

        //相关模型信息
        List<String> relatedModelItems = computableModel.getRelatedModelItems();
        computableModelResultDTO.setRelatedModelItemInfoList(genericService.getRelatedModelInfoList(relatedModelItems));

        //资源信息
        List<SimpleFileInfo> simpleFileInfoList = genericService.getSimpleFileInfoList(computableModel.getResources());
        computableModelResultDTO.setResourceJson(simpleFileInfoList);

        return computableModelResultDTO;
    }

    public JSONObject insert(List<MultipartFile> files, JSONObject jsonObject, String uid) {

        JSONObject result = new JSONObject();
        ComputableModel computableModel = new ComputableModel();

        String path = resourcePath + "/computableModel/" + jsonObject.getString("contentType");

        List<String> resources = new ArrayList<>();
        saveFiles(files, path, uid, "",resources);
        if (resources == null) {
            result.put("code", -1);
        } else {
            try {
                computableModel.setResources(resources);
                computableModel.setStatus(jsonObject.getString("status"));
                computableModel.setName(jsonObject.getString("name"));
                computableModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
                computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
                computableModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
                computableModel.setContentType(jsonObject.getString("contentType"));
                computableModel.setUrl(jsonObject.getString("url"));

                if(jsonObject.getString("image")!=null){
                    String imgPath="/computableModel/" + computableModel.getId() + "/title.jpg";
                    String image = jsonObject.getString("image");
                    String[] strs = image.split(",");
                    if(strs.length>1) {
                        String imgStr = strs[1];
                        Utils.base64StrToImage(imgStr, resourcePath + imgPath);
                        computableModel.setImage(imgPath);
                    } else {
                        computableModel.setImage("");
                    }
                }else{
                    computableModel.setImage(null);
                }

                ModelItem relateModelItem = modelItemDao.findFirstById(jsonObject.getString("relateModelItem"));
                computableModel.setClassifications(relateModelItem.getClassifications());

                String md5 = null;
                if (jsonObject.getString("contentType").equals("Package")) {
                    String filePath = path + resources.get(0);
                    File file = new File(filePath);

                    md5 = Utils.getMd5ByFile(file);

                    String mdlPath = null;
                    String testDataDirectoryPath = null;
                    String destDirPath = path + "/unZip/" + computableModel.getId();
                    ZipUtils.unZip(new File(filePath), destDirPath);
                    File unZipDir = new File(destDirPath);
                    if (unZipDir.exists()) {
                        LinkedList<File> list = new LinkedList<File>();
                        File[] dirFiles = unZipDir.listFiles();
                        for (File file2 : dirFiles) {
                            if (file2.isDirectory()) {
                                if (file2.getName().equals("testify")) {
                                    testDataDirectoryPath = file2.getAbsolutePath();
                                } else if (file2.getName().equals("model")) {
                                    list.add(file2);
                                }
                            } else {
                                String name = file2.getName();
                                if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                    mdlPath = file2.getAbsolutePath();
                                    break;
                                }
                                System.out.println("文件:" + file2.getAbsolutePath());
                            }
                        }
                        File temp_file;
                        while (!list.isEmpty()) {
                            temp_file = list.removeFirst();
                            dirFiles = temp_file.listFiles();
                            for (File file2 : dirFiles) {
                                if (file2.isDirectory()) {
                                    continue;
                                } else {
                                    String name = file2.getName();
                                    if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                        mdlPath = file2.getAbsolutePath();
                                        break;
                                    }
                                    System.out.println("文件:" + file2.getAbsolutePath());
                                }
                            }
                        }
                    } else {
                        System.out.println("文件不存在!");
                    }

                    //获取测试数据，并进行存储
                    if (testDataDirectoryPath != null) {
                        String testData = ModelServiceUtils.extractTestData(resourcePath, testDataDirectoryPath, computableModel.getId());
                        computableModel.setTestDataPath(testData);
                    } else {
                        computableModel.setTestDataPath("");
                    }

                    String content = "";
                    if (mdlPath != null) {
                        try {
                            BufferedReader in = new BufferedReader(new FileReader(mdlPath));
                            String str = in.readLine();
                            if (str.contains("ModelClass")) {
                                content += str;
                            }
                            while ((str = in.readLine()) != null) {
                                content += str;
                            }
                            in.close();
                            System.out.println(content);
                        } catch (IOException e) {
                            System.out.println(e);
                        }

                        computableModel.setMdl(content);
                        JSONObject mdlJson = XmlTool.documentToJSONObject(content);
//
//                        JSONArray HCinsert=modelClass.getJSONArray("Runtime").getJSONObject(0).getJSONArray("HardwareConfigures").getJSONObject(0).getJSONArray("INSERT");
//                        if(HCinsert!=null){
//
//                            JSONArray HCadd= new JSONArray();
//
//                            for(int j=0;j<HCinsert.size();j++){
//                                JSONObject obj=HCinsert.getJSONObject(j);
//                                if (obj.getJSONObject("key")!=null&&obj.getJSONObject("name")!=null){
//                                    HCadd.add(obj);
//                                }
//                            }
//
//                            runtime.getJSONArray("HardwareConfigures").getJSONObject(0).put("Add",HCadd);
//                        }
//
//                        JSONArray SCinsert=modelClass.getJSONArray("Runtime").getJSONObject(0).getJSONArray("SoftwareConfigures").getJSONObject(0).getJSONArray("INSERT");
//                        if(SCinsert!=null){
//
//                            JSONArray SCadd= new JSONArray();
//
//                            for(int j=0;j<HCinsert.size();j++){
//                                JSONObject obj=HCinsert.getJSONObject(j);
//                                if (obj.getJSONObject("key")!=null&&obj.getJSONObject("name")!=null){
//                                    SCadd.add(obj);
//                                }
//                            }
//
//                            runtime.getJSONArray("SoftwareConfigures").getJSONObject(0).put("Add",SCadd);
//                        }
//
//                        modelClass.getJSONArray("Runtime").remove(0);
//                        modelClass.getJSONArray("Runtime").add(runtime);
                        JSONObject modelClass = checkMdlJson(mdlJson);
                        mdlJson.getJSONArray("ModelClass").remove(0);
                        mdlJson.getJSONArray("ModelClass").add(modelClass);
                        //End
                        computableModel.setMdlJson(mdlJson);
                    } else {
                        System.out.println("mdl文件未找到!");
                    }

                    Utils.deleteDirectory(destDirPath);
                }
                else if(jsonObject.getString("contentType").toLowerCase().equals("md5")){
                    String mdl = jsonObject.getString("mdl");
                    computableModel.setMdl(jsonObject.getString("mdl"));
                    md5 = jsonObject.getString("md5");
                    computableModel.setPageConfigJson(JSONArray.parseArray(jsonObject.getString("pageConfig")));
                    JSONObject mdlJson = XmlTool.documentToJSONObject(mdl);
                    JSONObject modelClass = checkMdlJson(mdlJson);
                    if(mdlJson.containsKey("ModelClass")){
                        mdlJson.getJSONArray("ModelClass").remove(0);
                        mdlJson.getJSONArray("ModelClass").add(modelClass);
                    }
                    //End
                    computableModel.setMdlJson(mdlJson);
                    computableModel.setDeploy(true);
                }

                computableModel.setMd5(md5);

                computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));

                computableModel.setAuthor(uid);

                Date now = new Date();
                computableModel.setCreateTime(now);
                computableModel.setLastModifyTime(now);
                computableModelDao.insert(computableModel);

                //联动与计算模型相关的模型关联信息
                List<String> relatedModelItems = computableModel.getRelatedModelItems();
                for(int i=0;i<relatedModelItems.size();i++) {
                    String id = relatedModelItems.get(i);
                    ModelItem modelItem = modelItemDao.findFirstById(id);
                    ModelItemRelate modelItemRelate = modelItem.getRelate();
                    modelItemRelate.getConceptualModels().add(computableModel.getId());
                    modelItem.setRelate(modelItemRelate);
                    modelItemDao.save(modelItem);
                }

                result.put("code", 1);
                result.put("id", computableModel.getId());
            } catch (Exception e) {
                logger.error("计算模型创建错误",e);
                result.put("code", -2);
            }
        }
        return result;
    }

    public JSONObject update(List<MultipartFile> files, JSONObject jsonObject, String email) {
        JSONObject result = new JSONObject();
        ComputableModel computableModel_ori = computableModelDao.findFirstById(jsonObject.getString("id"));
        String author0 = computableModel_ori.getAuthor();
        ComputableModel computableModel = new ComputableModel();
        BeanUtils.copyProperties(computableModel_ori, computableModel);
        String contentType = jsonObject.getString("contentType");

        if (!computableModel_ori.isLock()) {
            String path = resourcePath + "/computableModel/" + jsonObject.getString("contentType");
            //如果上传新文件
            if (files.size() > 0) {
                List<String> resources =new ArrayList<>();
                saveFiles(files, path, email, "",resources);
                if (resources == null) {
                    result.put("code", -1);
                    return result;
                }
                computableModel.setResources(resources);
                computableModel.setMdlJson(null);
                try {
                    String md5 = null;
                    if (jsonObject.getString("contentType").equals("Package")) {
                        String filePath = path + resources.get(0);
                        FileInputStream file = new FileInputStream(filePath);
                        md5 = DigestUtils.md5DigestAsHex(IOUtils.readFully(file, -1, true));

                        String mdlPath = null;
                        String testDataDirectoryPath = null;
                        String destDirPath = path + "/unZip/" + computableModel.getId();
                        ZipUtils.unZip(new File(filePath), destDirPath);
                        File unZipDir = new File(destDirPath);
                        if (unZipDir.exists()) {
                            LinkedList<File> list = new LinkedList<File>();
                            File[] dirFiles = unZipDir.listFiles();
                            for (File file2 : dirFiles) {
                                if (file2.isDirectory()) {
                                    if (file2.getName().equals("testify")) {
                                        testDataDirectoryPath = file2.getAbsolutePath();
                                    } else if (file2.getName().equals("model")) {
                                        list.add(file2);
                                    }
                                } else {
                                    String name = file2.getName();
                                    if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                        mdlPath = file2.getAbsolutePath();
                                        break;
                                    }
                                    System.out.println("文件:" + file2.getAbsolutePath());
                                }
                            }
                            File temp_file;
                            while (!list.isEmpty()) {
                                temp_file = list.removeFirst();
                                dirFiles = temp_file.listFiles();
                                for (File file2 : dirFiles) {
                                    if (file2.isDirectory()) {
                                        continue;
                                    } else {
                                        String name = file2.getName();
                                        if (name.substring(name.length() - 3, name.length()).equals("mdl")) {
                                            mdlPath = file2.getAbsolutePath();
                                            break;
                                        }
                                        System.out.println("文件:" + file2.getAbsolutePath());
                                    }
                                }
                            }
                        } else {
                            System.out.println("文件不存在!");
                        }

                        //获取测试数据，并进行存储
                        if (testDataDirectoryPath != null) {
                            String testData = ModelServiceUtils.extractTestData(resourcePath, testDataDirectoryPath, computableModel.getId());
                            computableModel.setTestDataPath(testData);
                        } else {
                            computableModel.setTestDataPath("");
                        }

                        String content = "";
                        if (mdlPath != null) {
                            try {
                                BufferedReader in = new BufferedReader(new FileReader(mdlPath));
                                String str = in.readLine();
                                if (str.contains("ModelClass")) {
                                    content += str;
                                }
                                while ((str = in.readLine()) != null) {
                                    content += str;
                                }
                                in.close();
                                System.out.println(content);
                            } catch (IOException e) {
                                System.out.println(e);
                            }

                            computableModel.setMdl(content);
                            JSONObject mdlJson = XmlTool.documentToJSONObject(content);
                            computableModel.setMdlJson(mdlJson);
                        } else {
                            System.out.println("mdl文件未找到!");
                        }

                        Utils.deleteDirectory(destDirPath);
                    }
                    computableModel.setMd5(md5);

                    computableModel.setDeploy(false);

                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("code", -2);
                }
            }else if(contentType.toLowerCase().equals("md5")){
                String mdl = jsonObject.getString("mdl");
                computableModel.setMdl(mdl);
                String md5 = jsonObject.getString("md5");
                computableModel.setMd5(md5);
                computableModel.setPageConfigJson(JSONArray.parseArray(jsonObject.getString("pageConfig")));
                JSONObject mdlJson = XmlTool.documentToJSONObject(mdl);
                JSONObject modelClass = checkMdlJson(mdlJson);
                if(mdlJson.containsKey("ModelClass")){
                    mdlJson.getJSONArray("ModelClass").remove(0);
                    mdlJson.getJSONArray("ModelClass").add(modelClass);
                }
                //End
                computableModel.setMdlJson(mdlJson);

                computableModel.setDeploy(true);
            }

            String uploadImage = jsonObject.getString("image");
            if (uploadImage != null && !uploadImage.contains("/computableModel/") && !uploadImage.equals("")) {
                //删除旧图片
                File file = new File(resourcePath + computableModel.getImage());
                if (file.exists() && file.isFile())
                    file.delete();
                //添加新图片
                String imgPath = "/computableModel/" + computableModel.getId() + "/title.jpg";
                String imgStr = uploadImage.split(",")[1];
                Utils.base64StrToImage(imgStr, resourcePath + imgPath);
                computableModel.setImage(imgPath);
            }

            computableModel.setName(jsonObject.getString("name"));
            computableModel.setStatus(jsonObject.getString("status"));
            computableModel.setLocalizationList(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("localizationList"),Localization.class));
            computableModel.setAuthorships(ArrayUtils.parseJSONArrayToList(jsonObject.getJSONArray("authorship"),AuthorInfo.class));
            computableModel.setRelatedModelItems(jsonObject.getJSONArray("relatedModelItems").toJavaList(String.class));
            computableModel.setOverview(jsonObject.getString("description"));
            computableModel.setContentType(jsonObject.getString("contentType"));
            computableModel.setUrl(jsonObject.getString("url"));

            ModelItem relateModelItem = modelItemDao.findFirstById(jsonObject.getString("relateModelItem"));
            computableModel.setClassifications(relateModelItem.getClassifications());

            computableModel.setAuthor(email);

            Date now = new Date();
            String authorUserName = computableModel_ori.getAuthor();

            if (computableModel_ori.getAuthor().equals(email)) {
                computableModel.setLastModifyTime(now);
                computableModelDao.save(computableModel);

                result.put("method", "update");
                result.put("code", 1);
                result.put("id", computableModel.getId());
            } else {
                //TODO 计算模型版本
//                ComputableModelVersion computableModelVersion = new ComputableModelVersion();
//                BeanUtils.copyProperties(computableModel, computableModelVersion, "id");
//                computableModelVersion.setId(UUID.randomUUID().toString());
//                computableModelVersion.setOriginOid(computableModel_ori.getId());
//                computableModelVersion.setModifier(email);
//                computableModelVersion.setVerNumber(now.getTime());
//                computableModelVersion.setVerStatus(0);
//                userService.messageNumPlusPlus(authorUserName);
//
//                computableModelVersion.setModifyTime(now);
//                computableModelVersion.setCreator(author0);
//
//                computableModelVersionDao.save(computableModelVersion);
//
//                computableModel_ori.setLock(true);
//                computableModelDao.save(computableModel_ori);
//
//                result.put("method", "version");
//                result.put("code", 0);
//                result.put("id", computableModelVersion.getId());
            }
            return result;
        } else {
            return null;
        }
    }

    public JsonResult delete(String oid, String email) {
        ComputableModel computableModel = computableModelDao.findFirstById(oid);
        if (computableModel != null) {
            //删除资源
            String path = resourcePath + "/computableModel/" + computableModel.getContentType();
            List<String> resources = computableModel.getResources();
            for (int i = 0; i < resources.size(); i++) {
                Utils.delete(path + resources.get(i));
            }

            //计算模型删除
            computableModelDao.delete(computableModel);
            userService.ItemCountMinusOne(email, ItemTypeEnum.ComputableModel);

            //模型条目关联删除
            List<String> relatedModelItems = computableModel.getRelatedModelItems();
            for (int i = 0;i<relatedModelItems.size();i++) {
                String modelItemId = relatedModelItems.get(i);
                ModelItem modelItem = modelItemDao.findFirstById(modelItemId);
                List<String> computableModelIds = modelItem.getRelate().getComputableModels();
                for (String id : computableModelIds
                        ) {
                    if (id.equals(computableModel.getId())) {
                        computableModelIds.remove(id);
                        break;
                    }
                }
                modelItem.getRelate().setComputableModels(computableModelIds);
                modelItemDao.save(modelItem);
            }

            return ResultUtils.success();
        } else {
            return ResultUtils.error();
        }
    }
}
