package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONObject;
import njgis.opengms.portal.dao.*;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.dto.dataItem.DataItemUpdateDTO;
import njgis.opengms.portal.entity.po.DataHub;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author bin
 * @Date 2021/08/25
 */
@Service
public class DataHubService {

    @Autowired
    DataItemService dataItemService;

    @Autowired
    UserService userService;

    @Autowired
    ModelItemDao modelItemDao;

    @Autowired
    DataItemDao dataItemDao;

    @Autowired
    DataHubDao dataHubDao;

    @Autowired
    DataCategorysDao dataCategorysDao;

    @Autowired
    GenericService genericService;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    /**
     * 修改hub条目
     * @param dataItemUpdateDTO
     * @param email
     * @return com.alibaba.fastjson.JSONObject
     * @Author bin
     **/
    public JSONObject updateDataHubs(DataItemUpdateDTO dataItemUpdateDTO, String email) {
        JSONObject dao = genericService.daoFactory("dataHub");
        return dataItemService.updateItem(dataItemUpdateDTO,email,(GenericItemDao) dao.get("itemDao"));
    }

    public Page<DataHub> getUsersUploadDataHubs(String author, Integer page, Integer pagesize, Integer asc) {

        boolean as = false;
        if (asc == 1) {
            as = true;
        }

        Sort sort = Sort.by(as ? Sort.Direction.ASC : Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, pagesize, sort);
        return dataHubDao.findByAuthor(pageable, author);

    }


    /**
     * 删除dataHub
     * @param id
     * @param email
     * @return int
     * @Author bin
     **/
    public JsonResult delete(String id, String email){
        DataHub data = new DataHub();
        data = (DataHub) genericService.getById(id,dataItemDao);

        if(!data.getAuthor().equals(email))
            return ResultUtils.error("Unauthorized");

        // List<String> relatedModels = data.getRelatedModels();
        // if (relatedModels!=null)
        //     for (int i = 0; i < relatedModels.size(); i++) {
        //         ModelItem modelItem = modelItemDao.findFirstByOid(relatedModels.get(i));
        //         modelItem.getRelatedData().remove(id);
        //         modelItemDao.save(modelItem);
        //     }

        try {
            dataHubDao.deleteById(id);
            userService.updateUserResourceCount(email, "dataHub", "delete");
        }catch (Exception e){
            return ResultUtils.error("delete error");
        }

        return ResultUtils.success();
    }
}
