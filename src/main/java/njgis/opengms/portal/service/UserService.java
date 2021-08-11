package njgis.opengms.portal.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.bind.v2.TODO;
import njgis.opengms.portal.dao.UserDao;
import njgis.opengms.portal.entity.doo.AuthorInfo;
import njgis.opengms.portal.entity.doo.JsonResult;
import njgis.opengms.portal.entity.doo.MyException;
import njgis.opengms.portal.entity.doo.user.UserResourceCount;
import njgis.opengms.portal.entity.dto.user.UserShuttleDTO;
import njgis.opengms.portal.entity.po.DataItem;
import njgis.opengms.portal.entity.po.ModelItem;
import njgis.opengms.portal.entity.po.User;
import njgis.opengms.portal.enums.ItemTypeEnum;
import njgis.opengms.portal.utils.ResultUtils;
import njgis.opengms.portal.utils.Utils;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * @Description 用户业务层
 * @Author kx
 * @Date 2021/7/5
 * @Version 1.0.0
 */
@Service
public class UserService {

    @Value("${resourcePath}")
    private String resourcePath;

    @Value("${htmlLoadPath}")
    private String htmlLoadPath;

    @Value("${userServer}")
    private String userServer;

    @Value("${userServerCilent}")
    private String userServerCilent;

    @Value("${userServerCilentPWD}")
    private String userServerCilentPWD;

    @Autowired
    UserDao userDao;

    @Autowired
    TokenService tokenService;

    /**
     * @Description 用户相关条目计数 加一+++++
     * @param email 用户邮箱
     * @param itemType 条目类型
     * @Return void
     * @Author kx
     * @Date 2021/7/7
     **/
    public void ItemCountPlusOne(String email, ItemTypeEnum itemType){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount resourceCount = user.getResourceCount();
        int number = 1;
        resourceCount = changeItemCount(resourceCount, itemType, number);
        user.setResourceCount(resourceCount);
        userDao.save(user);
    }

    /**
     * @Description 用户相关条目计数 减一-----
     * @param email 用户邮箱
     * @param itemType 条目类型
     * @Return void
     * @Author kx
     * @Date 2021/7/7
     **/
    public void ItemCountMinusOne(String email, ItemTypeEnum itemType){
        User user = userDao.findFirstByEmail(email);
        UserResourceCount resourceCount = user.getResourceCount();
        int number = -1;
        resourceCount = changeItemCount(resourceCount, itemType, number);
        user.setResourceCount(resourceCount);
        userDao.save(user);
    }

    /**
     * @Description 改变对应类型的条目数量
     * @param resourceCount
     * @param itemType
     * @param number
     * @Return njgis.opengms.portal.entity.doo.user.UserResourceCount
     * @Author kx
     * @Date 2021/7/7
     **/
    public UserResourceCount changeItemCount(UserResourceCount resourceCount, ItemTypeEnum itemType, int number){
        switch (itemType){
            case ModelItem:
                resourceCount.setModelItem(resourceCount.getModelItem()+number);
                break;
            case ConceptualModel:
                resourceCount.setConceptualModel(resourceCount.getConceptualModel()+number);
                break;
            case LogicalModel:
                resourceCount.setLogicalModel(resourceCount.getLogicalModel()+number);
                break;
            case ComputableModel:
                resourceCount.setConceptualModel(resourceCount.getComputableModel()+number);
                break;
            case Concept:
                resourceCount.setConcept(resourceCount.getConcept()+number);
                break;
            case SpatialReference:
                resourceCount.setSpatial(resourceCount.getSpatial()+number);
                break;
            case Template:
                resourceCount.setTemplate(resourceCount.getTemplate()+number);
                break;
            case Unit:
                resourceCount.setUnit(resourceCount.getUnit()+number);
                break;
            case Theme:
                resourceCount.setTheme(resourceCount.getTheme()+number);
                break;
            case DataItem:
                resourceCount.setDataItem(resourceCount.getDataItem()+number);
                break;
            case DataHub:
                resourceCount.setDataHub(resourceCount.getDataHub()+number);
                break;
            case DataMethod:
                resourceCount.setDataMethod(resourceCount.getDataMethod()+number);
                break;
            // case Article:
            //     resourceCount.setArticle(resourceCount.getArticle()+number);
            //     break;
            // case Project:
            //     resourceCount.setProject(resourceCount.getProject()+number);
            //     break;
            // case Conference:
            //     resourceCount.setConference(resourceCount.getConference()+number);
            //     break;
        }
        return resourceCount;
    }

    /**
     * @Description 使用session记录用户登录状态
     * @param request
     * @param email
     * @param name
     * @Return void
     * @Author kx
     * @Date 2021/7/6
     **/
    public void setUserSession(HttpServletRequest request, String email, String name, String role){
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(120*60);//设置session过期时间 为120分钟
//            session.setAttribute("uid", result.getString("userName"));
        session.setAttribute("email", email);
        session.setAttribute("name", name);
        session.setAttribute("role", role);
    }

    /**
     * @Description 清除session中记录的登录信息
     * @param request
     * @Return void
     * @Author kx
     * @Date 2021/7/6
     **/
    public void removeUserSession(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("email");
        session.removeAttribute("name");
        session.removeAttribute("role");
    }

    /**
     * @Description 通过用户服务器进行登录
     * @param account email
     * @param password sha256加密后的密码
     * @param ip 用户登录ip
     * @Return com.alibaba.fastjson.JSONObject
     * @Author wzh
     * @Date 2021/7/6
     **/
    public JSONObject loginUserServer(String account, String password, String ip){
        JSONObject j_tokenInfo = tokenService.getTokenUsePwd(account, password);

        UserShuttleDTO userShuttleDTO = new UserShuttleDTO();
        if (j_tokenInfo == null){
            return null;
        }
        String access_token = (String)j_tokenInfo.get("access_token");

        //获取到用户服务器对象
        try{
            JSONObject userBaseJson = tokenService.logintoUserServer(access_token,ip).getJSONObject("data");
            userShuttleDTO = JSONObject.toJavaObject(userBaseJson, UserShuttleDTO.class);

            //更新该user的token
            User user = userDao.findFirstByEmail(account);
            if(user==null){
                User newUser = new User();
                String name = userShuttleDTO.getName();
                newUser.setId(userShuttleDTO.getUserId());
                newUser.setEmail(userShuttleDTO.getEmail());
                newUser.setAccessId(Utils.generateAccessId(name, userDao.findAllByAccessIdContains(name), true));
                userDao.insert(newUser);
            }
            user = userDao.findFirstByEmail(account);

            user.setName(userShuttleDTO.getName());
            user.setAvatar(userShuttleDTO.getAvatar());


            //TODO wzh 没看懂
            tokenService.refreshUserTokenInfo(j_tokenInfo,user);

            JSONObject ipUpdate = new JSONObject();
            ipUpdate.put("loginIp",ip);

            updatePartInfotoUserServer(userShuttleDTO.getEmail(),ipUpdate);

            JSONObject j_userShuttleDTO = (JSONObject) JSONObject.toJSON(userShuttleDTO);

            //我将返回值改成了email,name,role 2021.7.7
            JSONObject result = new JSONObject();
            result.put("email",j_userShuttleDTO.getString("email"));
            result.put("name",j_userShuttleDTO.getString("name"));
            result.put("role",user.getUserRole().getRole());

            return j_userShuttleDTO;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @Description 根据门户用户信息更新用户服务器用户
     * @param email
     * @param updateInfo
     * @Return int
     * @Author wzh
     * @Date 2021/7/6
     **/
    public int updatePartInfotoUserServer(String email,JSONObject updateInfo) throws ParseException, IOException, URISyntaxException, IllegalAccessException {

        try {
            String token = tokenService.checkToken(email);
            if(token.equals("out")){
                return -1;
            }

            RestTemplate restTemplate = new RestTemplate();
//            LinkedMultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
//            for (Map.Entry entry : jsonObject.entrySet()){
//                String filedName =  (String)entry.getKey();
//                valueMap.add(filedName, entry.getValue());
//            }
            String url = "http://" + userServer + "/AuthServer/user/add";
            HttpHeaders httpHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
            httpHeaders.setContentType(mediaType);
            httpHeaders.add("Authorization","Bearer " + token);

            HttpEntity<Object> httpEntity = new HttpEntity<>(updateInfo.toString(), httpHeaders);
            ResponseEntity<JSONObject> registerResult = restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);

            User user = userDao.findFirstByEmail(email);
            updatePortaluser(registerResult.getBody().getJSONObject("data"),user);

            int resCode = (int) registerResult.getBody().get("code");

            return resCode;
            // Confirm
//            User user = JSONObject.toJavaObject(jsonObject, User.class);
//            Query query = new Query(Criteria.where("userId").is(user.getUserId()));
//            User user1 = mongoTemplate.findOne(query, User.class);
//            if(user1 != null) return ResultUtils.error(-3, "Fail: user already exists in the database.");

//            return ResultUtils.success(mongoTemplate.save(user));

        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * @Description 根据用户服务器信息更新门户用户
     * @param updateUserInfo
     * @param portalUser
     * @Return void
     * @Author wzh
     * @Date 2021/7/6
     **/
    public void updatePortaluser(JSONObject updateUserInfo,User portalUser) throws NoSuchFieldException, IllegalAccessException {

        try {
            UserShuttleDTO userShuttleDTO = updateUserInfo.toJavaObject(UserShuttleDTO.class);

            if(portalUser==null){
                portalUser = new User();
            }
            for (Field shuttleField : userShuttleDTO.getClass().getDeclaredFields()) {
                String keyName = shuttleField.getName();
                Field field = null;
                try{
                    field = portalUser.getClass().getDeclaredField(keyName);
                    shuttleField = userShuttleDTO.getClass().getDeclaredField(keyName);
                }catch (Exception e){

                }
                if(field != null){
                    field.setAccessible(true);
                    shuttleField.setAccessible(true);
                    if (field.get(portalUser)!=null&&!field.get(portalUser).equals(shuttleField.get(userShuttleDTO))) {
                        if(shuttleField.getName().equals("avatar")){//用户头像单独处理
                            String avatar = (String) shuttleField.get(userShuttleDTO);
                            avatar = "http://" + userServer + "/userServer" + avatar;
                            field.set(portalUser, avatar);
                        }else{
                            field.set(portalUser, shuttleField.get(userShuttleDTO));
                        }
                    }
                }
            }
            userDao.save(portalUser);
        }catch (Exception e){

        }
    }


    /**
     * @Description 加载用户基础信息（邮箱，昵称，头像）
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JsonResult loadUser(String email) throws Exception {

        JSONObject userInfo = new JSONObject();

        if (email == null) {

            return ResultUtils.error(-2,"email is null");
        }

        User user = userDao.findFirstByEmail(email);

        //读取用户服务器上存储的用户基本信息
        JSONObject j_userBaseInfo = getUserBasicInfo(user.getEmail());

        if(j_userBaseInfo == null){
            return ResultUtils.error(-3, "can't get user info from user server");
        }else{
            userInfo.put("email", email);
            userInfo.put("name", j_userBaseInfo.getString("name"));
            userInfo.put("avatar", j_userBaseInfo.getString("avatar"));
        }

        return ResultUtils.success(userInfo);
    }

    /**
     * @Description 根据email从用户服务器获取用户信息
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getUserBasicInfo(String email) throws Exception {
        User user = userDao.findFirstByEmail(email);
        JSONObject jsonObject = new JSONObject();
        if(user!=null){
            String token = tokenService.checkToken(email);
            if(token.equals("out")){
                return null;
            }
            jsonObject = tokenService.getBasicInfo(token);
            JSONObject j_userInfo = new JSONObject();
            if(jsonObject!=null){

                j_userInfo = jsonObject.getJSONObject("data");
                String avatar = j_userInfo.getString("avatar");
                if(avatar!=null){
                    avatar = "http://" + userServer + "/userServer" + avatar;
                }
                j_userInfo.put("avatar",avatar);
            }else{
                return null;
            }
            return j_userInfo;

        }else{
            return null;
        }

    }

    /**
     * @Description 从用户服务器获取用户基础信息 TODO wzh 基础信息包括哪些？
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getInfoFromUserServer(String email){
        JSONObject jsonObject = new JSONObject();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String userInfoUrl = "http://" + userServer + "/user/" + email + "/" + userServerCilent + "/" + userServerCilentPWD;
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json;charset=UTF-8");
            headers.setContentType(mediaType);
            headers.set("user-agent", "portal_backend");
            HttpEntity httpEntity = new HttpEntity(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, httpEntity, JSONObject.class);
            JSONObject userInfo = response.getBody().getJSONObject("data");

            String avatar = userInfo.getString("avatar");
            if(avatar!=null){
                avatar = "/userServer" + avatar;
            }
            userInfo.put("avatar",avatar);
            userInfo.put("msg","suc");
            return userInfo;
        }catch(Exception e){
            System.out.println(e.fillInStackTrace());
            jsonObject.put("msg","no user");
        }
        return jsonObject;
    }

    /**
     * @Description 从用户服务器和门户获取用户全部信息 TODO 是否要去掉密码？
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getFullUserInfo(String email) throws Exception {
        JSONObject commonInfo = getInfoFromUserServer(email);
        JSONObject j_result = new JSONObject();
        if (commonInfo.getString("msg").equals("suc")){
            j_result.putAll(commonInfo);

            User user = userDao.findFirstByEmail(email);

            j_result.put("userId", user.getAccessId());
            j_result.put("email", user.getEmail());
            j_result.put("phone", user.getPhone());
            j_result.put("weChat", user.getWeChat());
            j_result.put("faceBook", user.getFaceBook());
            j_result.put("lab", user.getLab());
            j_result.put("externalLinks", user.getExternalLinks());
            j_result.put("eduExperiences", user.getEducationExperiences());
            j_result.put("awdHonors", user.getAwardsHonors());
            j_result.put("runTask", user.getRunTask());
            j_result.put("image", user.getAvatar().equals("") ? "" : htmlLoadPath + user.getAvatar());
            j_result.put("subscribe", user.getSubscribe());

        }else {
            j_result = commonInfo;
        }

        return j_result;
    }

    /**
     * @Description 从用户服务器获取用户文件资源信息
     * @param email
     * @Return com.alibaba.fastjson.JSONObject
     * @Author kx
     * @Date 2021/7/6
     **/
    public JSONObject getUserResource(String email) throws Exception {
        JSONObject j_userInfo = getInfoFromUserServer(email);

        JSONObject j_result = new JSONObject();
        if (j_userInfo.getString("msg").equals("suc")){
            JSONArray resource = j_userInfo.getJSONArray("resource");
            JSONObject obj = new JSONObject();
            obj.put("uid", "0");
            obj.put("name", "All Folder");
            obj.put("children", resource);

            j_result.put("resource",obj);
            j_result.put("msg","suc");

        } else {
            j_result = j_userInfo;
        }

        return j_result;
    }


    public JSONObject getItemUserInfoByEmail(String email) {
        User user = userDao.findFirstByEmail(email);
        JSONObject userInfo = getInfoFromUserServer(user.getEmail());
        JSONObject userJson = new JSONObject();
        userJson.put("name", userInfo.getString("name"));
        // userJson.put("oid", user.getOid());
        userJson.put("email", user.getEmail());
        userJson.put("accessId", user.getAccessId());
        // userJson.put("image", user.getAvatar().equals("") ? "" : htmlLoadPath + user.getAvatar());
        userJson.put("image", userInfo.getString("avatar"));
        return userJson;
    }


}
