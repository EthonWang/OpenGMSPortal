package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.*;
import njgis.opengms.portal.entity.doo.data.InvokeService;
import njgis.opengms.portal.entity.dto.dataItem.DataItemFindDTO;
import njgis.opengms.portal.entity.po.*;
import njgis.opengms.portal.enums.ResultEnum;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/04
 */
@Service
public class DataItemService {

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    UserDao userDao;

    @Autowired
    DataCategorysDao dataCategorysDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    DataMethodDao dataMethodDao;

    // private GenericItemDao genericDataItemDao;


    @Autowired
    GenericService genericService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    /**
     * @Description 根据传入的id返回dataItem的详情界面
     * @Param [id]
     * @return org.springframework.web.servlet.ModelAndView
     **/
    public ModelAndView getPage(String id, GenericItemDao genericItemDao){
        ModelAndView view = new ModelAndView();

        DataItem dataItem;
        try {
            dataItem =  (DataItem) genericService.getById(id,genericItemDao);
        }catch (MyException e){
            view.setViewName("error/404");
            return view;
        }

        dataItem = (DataItem)genericService.recordViewCount(dataItem);
        genericItemDao.save(dataItem);

        //用户信息

        JSONObject userJson = userService.getItemUserInfoByEmail(dataItem.getAuthor());

        //authorship
        String authorshipString="";
        List<AuthorInfo> authorshipList=dataItem.getAuthorships();
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
        //related models
        JSONArray modelItemArray=new JSONArray();
        List<String> relatedModels=dataItem.getRelatedModels();
        if(relatedModels!=null) {
            for (String mid : relatedModels) {
                try {
                    ModelItem modelItem = modelItemDao.findFirstById(mid);
                    JSONObject modelItemJson = new JSONObject();
                    modelItemJson.put("name", modelItem.getName());
                    modelItemJson.put("id", modelItem.getId());
                    modelItemJson.put("description", modelItem.getOverview());
                    modelItemJson.put("image", modelItem.getImage().equals("") ? null : htmlLoadPath + modelItem.getImage());
                    modelItemArray.add(modelItemJson);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> fileName = new ArrayList<>();
        if (null!=dataItem.getDataType()&&dataItem.getDataType().equals("DistributedNode")){
            fileName.add(dataItem.getName());
        }
        //设置远程数据内容
        List<InvokeService> invokeServices = dataItem.getInvokeServices();


        //排序
        List<Localization> locals = dataItem.getLocalizationList();
        Collections.sort(locals);

        String detailResult = "";
        String detailLanguage = "";
        //先找中英文描述
        for(Localization localization:locals){
            String local = localization.getLocalCode();
            if(local.equals("en")||local.equals("zh")||local.contains("en-")||local.contains("zh-")){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }
        //如果没有中英文，则使用其他语言描述
        if(detailResult.equals("")){
            for(Localization localization:locals){
                String localDesc = localization.getDescription();
                if(localDesc!=null&&!localDesc.equals("")) {
                    detailLanguage = localization.getLocalName();
                    detailResult = localization.getDescription();
                    break;
                }
            }
        }

        //语言列表
        List<String> languageList = new ArrayList<>();
        for(Localization local:locals){
            languageList.add(local.getLocalName());
        }


        view.setViewName("data_item_info");
        view.addObject("datainfo", ResultUtils.success(dataItem));
        view.addObject("user",userJson);
        view.addObject("classifications",dataItem.getClassifications());
        view.addObject("relatedModels",modelItemArray);
        view.addObject("authorship",authorshipString);
        view.addObject("fileName",fileName);//后期应该是放该name下的所有数据
        view.addObject("distributeData", invokeServices);//存放远程节点信息
        //多语言description
        view.addObject("detailLanguage",detailLanguage);
        view.addObject("itemType","Data");
        view.addObject("languageList",languageList);
        view.addObject("itemInfo",dataItem);
        view.addObject("detail",detailResult);

        return view;

    }



    /**
     * 获取当前条目的远程数据信息
     * @return 成功失败或者远程数据信息
     */
    public List<InvokeService> getDistributeDataInfo(@PathVariable(value = "dataItemId") String dataItemId){
        DataItem dataItem = dataItemDao.findFirstById(dataItemId);
        return dataItem.getInvokeServices();
    }

    /**
     * @Description 获取与数据条目相关的模型
     * @Param [id]
     * @return com.alibaba.fastjson.JSONArray
     **/
    public JSONArray getRelation(String id) {

        JSONArray result = new JSONArray();
        DataItem dataItem = dataItemDao.findFirstById(id);
        List<String> relatedModels = dataItem.getRelatedModels();

        for (String modelId : relatedModels) {
            ModelItem modelItem = modelItemDao.findFirstById(modelId);
            JSONObject object = new JSONObject();
            object.put("id", modelItem.getId());
            object.put("name", modelItem.getName());
            User user = userDao.findFirstByEmail(modelItem.getAuthor());
            object.put("author", user.getName());
            object.put("author_email", user.getEmail());
            result.add(object);
        }

        return result;
    }


    // TODO 这个方法逻辑有问题
    /**
     * @Description 设置与数据条目相关的模型
     * @Author bin
     * @Param [id, relations]
     * @return java.lang.String
     **/
    public String setRelation(String id, List<String> relations) {

        DataItem dataItem = dataItemDao.findFirstById(id);

        List<String> relationDelete = new ArrayList<>();
        for (int i = 0; i < dataItem.getRelatedModels().size(); i++) {
            relationDelete.add(dataItem.getRelatedModels().get(i));
        }
        List<String> relationAdd = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++) {
            relationAdd.add(relations.get(i));
        }

        for (int i = 0; i < relationDelete.size(); i++) {
            for (int j = 0; j < relationAdd.size(); j++) {
                if (relationDelete.get(i).equals(relationAdd.get(j))) {
                    relationDelete.set(i, "");
                    relationAdd.set(j, "");
                    break;
                }
            }
        }

        for (int i = 0; i < relationDelete.size(); i++) {
            String model_id = relationDelete.get(i);
            if (!model_id.equals("")) {
                ModelItem modelItem = modelItemDao.findFirstById(model_id);
                // TODO 为什么Private就可以加进去了
                if(modelItem.getStatus().equals("Private")){
                    relations.add(modelItem.getId());
                    continue;
                }
                if (modelItem.getRelate().getDataItems() != null) {
                    modelItem.getRelate().getDataItems().remove(id);
                    modelItemDao.save(modelItem);
                }
            }
        }

        for (int i = 0; i < relationAdd.size(); i++) {
            String model_id = relationAdd.get(i);
            if (!model_id.equals("")) {
                ModelItem modelItem = modelItemDao.findFirstById(model_id);
                if (modelItem.getRelate().getDataItems() != null) {
                    modelItem.getRelate().getDataItems().add(id);
                } else {
                    List<String> relatedData = new ArrayList<>();
                    relatedData.add(id);
                    modelItem.getRelate().setDataItems(relatedData);
                }
                modelItemDao.save(modelItem);
            }
        }

        dataItem.setRelatedModels(relations);
        dataItemDao.save(dataItem);

        return "suc";

    }




}
