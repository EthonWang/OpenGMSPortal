var createSpatialReference = Vue.extend({
    template: "#createSpatialReference",
    data() {
        return {
            status: "Public",

            defaultActive: '4-2',
            curIndex: '6',

            ScreenMaxHeight: "0px",
            IframeHeight: "0px",
            editorUrl: "",
            load: false,

            ScreenMinHeight: "0px",

            userId: "",
            userName: "",
            loginFlag: false,
            activeIndex: 2,

            userInfo: {
                //username:"",
                name: "",
                email: "",
                phone: "",
                insName: ""
            },

            treeData: [{
                id: 1,
                label: "Spatial Reference Repository",
                oid: '58340c92-d74f-4d81-8a80-e4fcff286008',
                children: [{
                    id: 100,
                    "oid": "da70ad83-de57-4fc3-a85d-c1dcf4961433",
                    "label": "Basic"
                },
                    {
                        id: 101,
                        "oid": "c4642926-e797-4f61-92d6-7933df2413d2",
                        "label": "EPSG"
                    },
                    {
                        id: 102,
                        "oid": "e8562394-b55f-46d7-870e-ef5ad3aaf110",
                        "label": "ESRI"
                    },
                    {
                        id: 103,
                        "oid": "ee830613-1603-4f38-a196-5028e4e10d39",
                        "label": "IAU"
                    },
                    {
                        id: 104,
                        "oid": "b2f2fbfd-f21a-47ac-9e1f-a96ac0218bf1",
                        "label": "Customized"
                    }]
            }, {
                id: 2,
                label: "Temporal Reference Repository",
                oid: 'ce37e343-bf2c-4e7b-902e-46616604e184',
                children: [{
                    id: 3,
                    label: "Date",
                    oid: '295d2120-402b-4ee6-a0b5-308b67fe2c40',
                },
                    {
                        id: 4,
                        label: "Time",
                        oid: '6883d3fb-8485-4771-9a3e-3276c759364e',
                    }]
            }],

            defaultProps: {
                children: 'children',
                label: 'label'
            },
            cls: [],
            clsStr: '',
            parId: "",

            referenceInfo: {},

            socket: "",

            spatialReference_oid: "",

            editType:"",//create,modify
            currentLocalization:{
                localCode:"",
                localName:"",
                name:"",
                description:"",
            },
            localIndex: 0,
            languageAdd:{
                show:false,
                local:{},
            },
            localizationList:[
                {
                    localCode:"en",
                    localName:"English",
                    name:"",
                    description:"",
                    selected:true,
                }
            ],
            languageList:[
                { value: 'af', label: 'Afrikaans' },
                { value: 'sq', label: 'Albanian' },
                { value: 'ar', label: 'Arabic' },
                { value: 'hy', label: 'Armenian' },
                { value: 'az', label: 'Azeri' },
                { value: 'eu', label: 'Basque' },
                { value: 'be', label: 'Belarusian' },
                { value: 'bg', label: 'Bulgarian' },
                { value: 'ca', label: 'Catalan' },
                { value: 'zh', label: 'Chinese' },
                { value: 'hr', label: 'Croatian' },
                { value: 'cs', label: 'Czech' },
                { value: 'da', label: 'Danish' },
                { value: 'dv', label: 'Divehi' },
                { value: 'nl', label: 'Dutch' },
                { value: 'en', label: 'English' },
                { value: 'eo', label: 'Esperanto' },
                { value: 'et', label: 'Estonian' },
                { value: 'mk', label: 'FYRO Macedonian' },
                { value: 'fo', label: 'Faroese' },
                { value: 'fa', label: 'Farsi' },
                { value: 'fi', label: 'Finnish' },
                { value: 'fr', label: 'French' },
                { value: 'gl', label: 'Galician' },
                { value: 'ka', label: 'Georgian' },
                { value: 'de', label: 'German' },
                { value: 'el', label: 'Greek' },
                { value: 'gu', label: 'Gujarati' },
                { value: 'he', label: 'Hebrew' },
                { value: 'hi', label: 'Hindi' },
                { value: 'hu', label: 'Hungarian' },
                { value: 'is', label: 'Icelandic' },
                { value: 'id', label: 'Indonesian' },
                { value: 'it', label: 'Italian' },
                { value: 'ja', label: 'Japanese' },
                { value: 'kn', label: 'Kannada' },
                { value: 'kk', label: 'Kazakh' },
                { value: 'kok', label: 'Konkani' },
                { value: 'ko', label: 'Korean' },
                { value: 'ky', label: 'Kyrgyz' },
                { value: 'lv', label: 'Latvian' },
                { value: 'lt', label: 'Lithuanian' },
                { value: 'ms', label: 'Malay' },
                { value: 'mt', label: 'Maltese' },
                { value: 'mi', label: 'Maori' },
                { value: 'mr', label: 'Marathi' },
                { value: 'mn', label: 'Mongolian' },
                { value: 'ns', label: 'Northern Sotho' },
                { value: 'nb', label: 'Norwegian' },
                { value: 'ps', label: 'Pashto' },
                { value: 'pl', label: 'Polish' },
                { value: 'pt', label: 'Portuguese' },
                { value: 'pa', label: 'Punjabi' },
                { value: 'qu', label: 'Quechua' },
                { value: 'ro', label: 'Romanian' },
                { value: 'ru', label: 'Russian' },
                { value: 'se', label: 'Sami' },
                { value: 'sa', label: 'Sanskrit' },
                { value: 'sr', label: 'Serbian' },
                { value: 'sk', label: 'Slovak' },
                { value: 'sl', label: 'Slovenian' },
                { value: 'es', label: 'Spanish' },
                { value: 'sw', label: 'Swahili' },
                { value: 'sv', label: 'Swedish' },
                { value: 'syr', label: 'Syriac' },
                { value: 'tl', label: 'Tagalog' },
                { value: 'ta', label: 'Tamil' },
                { value: 'tt', label: 'Tatar' },
                { value: 'te', label: 'Telugu' },
                { value: 'th', label: 'Thai' },
                { value: 'ts', label: 'Tsonga' },
                { value: 'tn', label: 'Tswana' },
                { value: 'tr', label: 'Turkish' },
                { value: 'uk', label: 'Ukrainian' },
                { value: 'ur', label: 'Urdu' },
                { value: 'uz', label: 'Uzbek' },
                { value: 'vi', label: 'Vietnamese' },
                { value: 'cy', label: 'Welsh' },
                { value: 'xh', label: 'Xhosa' },
                { value: 'zu', label: 'Zulu' },
            ],

            editType:'create',

            toCreate: 1,

            draft:{
                oid:'',
            },

            draftOid:'',

            startDraft:0,

            itemInfoImage:''
        }
    },
    methods: {
        addLocalization(){
            this.languageAdd.show = true;
        },
        confirmAddLocal(){

            if(this.languageAdd.local.label==undefined){
                this.$message({
                    message: 'Please selcet a language!',
                    type: 'warning'
                });
                return;
            }
            for(i=0;i<this.localizationList.length;i++){
                let localization = this.localizationList[i];
                if(localization.localName==this.languageAdd.local.label){
                    this.$message({
                        message: 'This language already exists!',
                        type: 'warning'
                    });
                    return;
                }
            }

            let newLocalization = {
                localCode:this.languageAdd.local.value,
                localName:this.languageAdd.local.label,
                name:"",
                description:"",
            };
            this.localizationList.push(newLocalization);
            this.languageAdd.local={};

            this.changeLocalization(newLocalization);
        },
        cancelAddLocal(){
            this.languageAdd.show = false;
        },
        deleteLocalization(row){
            this.$confirm('Do you want to delete <b>'+row.localName+'</b> description?', 'Warning', {
                dangerouslyUseHTMLString: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                type: 'warning'
            }).then(() => {

                for(i=0;i<this.localizationList.length;i++){
                    let localization = this.localizationList[i]
                    if(localization.localName==row.localName){
                        this.localizationList.splice(i,1);
                        break;
                    }
                }
                if(this.localizationList.length>0){
                    this.changeLocalization(this.localizationList[0]);
                }else{
                    this.changeLocalization(null);
                }

                this.$message({
                    type: 'success',
                    message: 'Delete '+row.localName+' successfully!',
                });
            }).catch(() => {

            });

        },
        changeLocalization(row){
            if(row==null){
                this.currentLocalization={
                    localCode:"",
                    localName:"",
                    name:"",
                    description:"",
                };
                tinymce.activeEditor.setContent("");
                // tinymce.undoManager.clear();
            }else {
                for (i = 0; i < this.localizationList.length; i++) {
                    this.$set(this.localizationList[i], "selected", false);
                    if (this.currentLocalization.localName == this.localizationList[i].localName) {
                        this.localizationList[i].name = this.currentLocalization.name;
                        this.localizationList[i].description = tinymce.activeEditor.getContent();
                    }
                }
                this.$set(row, "selected", true);
                this.currentLocalization = row;
                tinymce.activeEditor.setContent(row.description);
                // tinymce.undoManager.clear();
            }
        },

        initLocalization(row){
            this.$set(row, "selected", true);
            this.currentLocalization.localName = row.localName
            this.currentLocalization.name = row.name
            tinymce.activeEditor.setContent(row.description);
        },

        changeRter(index) {
            this.curIndex = index;
            var urls = {
                1: '/user/userSpace',
                2: '/user/userSpace/model',
                3: '/user/userSpace/data',
                4: '/user/userSpace/server',
                5: '/user/userSpace/task',
                6: '/user/userSpace/community',
                7: '/user/userSpace/theme',
                8: '/user/userSpace/account',
                9: '/user/userSpace/feedback',
            }

            this.setSession('curIndex', index)
            window.location.href = urls[index]

        },
        handleSelect(index, indexPath) {
            this.setSession("index", index);
            window.location.href = "/user/userSpace"
        },
        handleCheckChange(data, checked, indeterminate) {
            let checkedNodes = this.$refs.tree2.getCheckedNodes()
            let classes = [];
            let str = '';
            for (let i = 0; i < checkedNodes.length; i++) {
                // console.log(checkedNodes[i].children)
                if (checkedNodes[i].children != undefined) {
                    continue;
                }

                classes.push(checkedNodes[i].oid);
                str += checkedNodes[i].label;
                if (i != checkedNodes.length - 1) {
                    str += ", ";
                }
            }
            this.cls = classes;
            this.clsStr = str;

        },
        changeOpen(n) {
            this.activeIndex = n;
        },

        insertInfo(basicInfo){
            let user_num = 0;

            this.referenceInfo = basicInfo;

            //cls
            this.cls = basicInfo.classifications;
            this.status = basicInfo.status;
            let ids = [];
            for (i = 0; i < this.cls.length; i++) {
                for (j = 0; j < 1; j++) {
                    for (k = 0; k < this.treeData[j].children.length; k++) {
                        if (this.cls[i] == this.treeData[j].children[k].oid) {
                            ids.push(this.treeData[j].children[k].id);
                            this.parid = this.treeData[j].children[k].id;
                            this.clsStr += this.treeData[j].children[k].label;
                            if (i != this.cls.length - 1) {
                                this.clsStr += ", ";
                            }
                            break;
                        }
                    }
                    if (ids.length - 1 == i) {
                        break;
                    }
                }
            }

            this.$refs.tree2.setCheckedKeys(ids);

            $(".providers").children(".panel").remove();
            $("#wknameInput").val(basicInfo.wkname);
            $("#wktInput").val(basicInfo.wkt);
            $("#nameInput").val(basicInfo.name);
            $("#descInput").val(basicInfo.description);

            //image
            if (basicInfo.image != "") {
                this.itemInfoImage = basicInfo.image
            }

            //detail
            this.localizationList = basicInfo.localizationList;
            initTinymce('textarea#conceptText',this.initLocalization,this.localizationList[0])

            //alias
            $('#aliasInput').tagEditor('destroy');
            $('#aliasInput').tagEditor({
                initialTags: basicInfo.alias ,
                forceLowercase: false,
                // placeholder: 'Enter alias ...'
            });




        },

        getItemContent(trigger,callBack){
            let itemObj = {}

            itemObj.classifications = this.cls;
            itemObj.name = $("#nameInput").val();
            let aliasInputValue = $("#aliasInput").val();
            if(aliasInputValue!=="") {
                itemObj.alias = $("#aliasInput").val().split(",");
            }
            itemObj.status = this.status;
            itemObj.wkname = $("#wknameInput").val();
            itemObj.wkt = $("#wktInput").val();
            itemObj.overview = $("#descInput").val();
            itemObj.uploadImage = this.itemInfoImage
            itemObj.localizationList = this.localizationList;

            if(this.editType == 'modify') {

                for (i = 0; i < this.localizationList.length; i++) {
                    if (this.currentLocalization.localName == this.localizationList[i].localName) {
                        this.localizationList[i].name = this.currentLocalization.name;
                        this.localizationList[i].description = tinymce.activeEditor.getContent();
                        break;
                    }
                }
                itemObj.localizationList = this.localizationList;

            }else {
                itemObj.localizationList = [];

                this.currentLocalization.description = tinymce.activeEditor.getContent();
                this.currentLocalization.localCode = this.languageAdd.local.value;
                this.currentLocalization.localName = this.languageAdd.local.label;

                itemObj.localizationList.push(this.currentLocalization);
            }

            if(callBack){
                callBack(itemObj)
            }

            return itemObj;
        },

        imgFile() {
            $("#imgOne").click();
        },

        preImg() {


            var file = $('#imgOne').get(0).files[0];
            //创建用来读取此文件的对象
            var reader = new FileReader();
            //使用该对象读取file文件
            reader.readAsDataURL(file);
            //读取文件成功后执行的方法函数
            reader.onload =  (e) => {
                //读取成功后返回的一个参数e，整个的一个进度事件
                //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
                //的base64编码格式的地址
                this.itemInfoImage = e.target.result
            }


        },

        deleteImg(){
            let obj = document.getElementById('imgOne')
            // obj.outerHTML=obj.outerHTML
            obj.value = ''
            this.itemInfoImage = ''
        },

        //draft
        onInputName(){
            console.log(1)
            if(this.toCreate==1){
                this.toCreate=0
                this.timeOut=setTimeout(()=>{
                    this.toCreate=1
                    this.startDraft=1
                },30000)
                setTimeout(()=>{
                    this.createDraft()
                },300)

            }
        },

        getStep(){
            let domID=$('.step-tab-panel.active')[0].id
            return parseInt(domID.substring(domID.length-1,domID.length))
        },

        createDraft(){//请求后台创建一个草稿实例,如果存在则更新

            var step = this.getStep()
            let content=this.getItemContent(step)

            // let urls=window.location.href.split('/')
            // let item=urls[6]
            // item=item.substring(6,item.length)
            item="SpatialReference"
            let obj={
                content:content,
                editType:this.editType,
                itemType:item,
                user:this.userId,
                oid:this.draft.oid,
            }
            if(this.editType) {
                obj.itemOid=this.$route.params.editId?this.$route.params.editId:null
                obj.itemName= this.itemName;
            }

            this.$refs.draftBox.createDraft(obj)
        },

        loadMatchedCreateDraft(){
            this.$refs.draftBox.loadMatchedCreateDraft()
        },

        deleteDraft(){
            this.$refs.draftBox.deleteDraft(this.draft.oid)
        },

        initDraft(editType,backUrl,oidFrom,oid){
            this.$refs.draftBox.initDraft(editType,backUrl,oidFrom,oid)
        },

        getDraft(){
            return this.$refs.draftBox.getDraft();
        },

        insertDraft(draftContent){
            this.insertInfo(draftContent)
        },

        cancelEditClick(){
            if(this.getDraft()!=null){
                this.$refs.draftBox.cancelDraftDialog=true
            }else{
                setTimeout(() => {
                    window.location.href = "/user/userSpace#/communities/dataTemplate";
                }, 305)
            }
        },

        draftJump(){
            window.location.href = '/user/userSpace#/communities/dataTemplate';
        },

        setSession(name, value) {
            window.sessionStorage.setItem(name, value);
        },

        sendcurIndexToParent() {
            this.$emit('com-sendcurindex', this.curIndex)
        },

        sendUserToParent(userId) {
            this.$emit('com-senduserinfo', userId)
        },

        init: function () {

            // if ('WebSocket' in window) {
            //     // this.socket = new WebSocket("ws://localhost:8080/websocket");
            //     this.socket = new WebSocket(websocketAddress);
            //     // 监听socket连接
            //     this.socket.onopen = this.open;
            //     // 监听socket错误信息
            //     this.socket.onerror = this.error;
            //     // 监听socket消息
            //     this.socket.onmessage = this.getMessage;
            //
            // }
            // else {
            //     alert('当前浏览器 Not support websocket');
            //     console.log("websocket 无法连接");
            // }
        },
        open: function () {
            console.log("socket连接成功")
        },
        error: function () {
            console.log("连接错误");
        },
        getMessage: function (msg) {
            console.log(msg.data);
        },
        send: function (msg) {
            this.socket.send(msg);
        },
        close: function () {
            console.log("socket已经关闭")
        },
        getnoticeNum(spatialReference_oid) {
            this.message_num_socket = 0;//初始化消息数目
            let data = {
                type: 'spatialReference',
                oid: spatialReference_oid,
            };

            //根据oid去取该作者的被编辑的条目数量
            $.ajax({
                url: "/theme/getAuthornoticeNum",
                type: "GET",
                data: data,
                async: false,
                success: (json) => {
                    this.message_num_socket = json;
                }
            });
            let data_theme = {
                type: 'spatialReference',
                oid: spatialReference_oid,
            };
            $.ajax({
                url: "/theme/getThemenoticeNum",
                async: false,
                type: "GET",
                data: data_theme,
                success: (json) => {
                    console.log(json);
                    for (let i = 0; i < json.length; i++) {
                        for (let k = 0; k < 4; k++) {
                            let type;
                            switch (k) {
                                case 0:
                                    type = json[i].subDetails;
                                    break;
                                case 1:
                                    type = json[i].subClassInfos;
                                    break;
                                case 2:
                                    type = json[i].subDataInfos;
                                    break;
                                case 3:
                                    type = json[i].subApplications;
                                    break;
                            }
                            if (type != null && type.length > 0) {
                                for (let j = 0; j < type.length; j++) {
                                    if (k == 0) {
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    } else if (k == 1) {
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    } else if (k == 2) {
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    } else if (k == 3) {
                                        switch (type[j].status) {
                                            case "0":
                                                this.message_num_socket++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }

    },
    mounted() {
        let that = this;
        that.init();
        //初始化的时候吧curIndex传给父组件，来控制bar的高亮显示
        this.sendcurIndexToParent()

        $(() => {
            let height = document.documentElement.clientHeight;
            this.ScreenMinHeight = (height) + "px";
            this.ScreenMaxHeight = (height) + "px";

            window.onresize = () => {
                console.log('come on ..');
                height = document.documentElement.clientHeight;
                this.ScreenMinHeight = (height) + "px";
                this.ScreenMaxHeight = (height) + "px";
            };
        })

        var oid = this.$route.params.editId;//取得所要edit的id
        this.draft.oid=window.localStorage.getItem('draft');//取得保存的草稿的Oid

        var user_num = 0;

        if ((oid === "0") || (oid === "") || (oid === null) || (oid === undefined)) {

            // $("#title").text("Create Spatial Reference")
            $("#subRteTitle").text("/Create Spatiotemporal Reference")

            $('#aliasInput').tagEditor({
                forceLowercase: false
            });

            this.editType = 'create';

            let interval = setInterval(function () {
                initTinymce('textarea#singleDescription')
                clearInterval(interval);
            },500);

            this.$set(this.languageAdd.local,"value","en");
            this.$set(this.languageAdd.local,"label","English");

            if(this.draft.oid!=''&&this.draft.oid!=null&&typeof (this.draft.oid)!="undefined"){
                // this.loadDraftByOid()
                this.initDraft('create','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }else{
                this.loadMatchedCreateDraft();
            }
        }
        else {

            this.editType = "modify";
            if(this.draft.oid==''||this.draft.oid==null||typeof (this.draft.oid)=="undefined"){
                this.initDraft('edit','/user/userSpace#/models/modelitem','item',this.$route.params.editId)
            }else{
                this.initDraft('edit','/user/userSpace#/models/modelitem','draft',this.draft.oid)
            }
            // $("#title").text("Modify Spatial Reference")
            $("#subRteTitle").text("/Modify Spatiotemporal Reference")
            // document.title = "Modify Spatial Reference | OpenGMS"
            if(window.localStorage.getItem('draft')==null) {
                $.ajax({
                    url: "/spatialReference/itemInfo/" + oid,
                    type: "get",
                    data: {},

                    success: (result) => {
                        console.log(result)
                        var basicInfo = result.data;

                        this.insertInfo(basicInfo)


                    }
                })
            }
            $('#aliasInput').tagEditor({
                forceLowercase: false
            });
            window.sessionStorage.setItem("editSpatial_id", "");
        }

        window.localStorage.removeItem('draft');

        $("#step").steps({
            onFinish: function () {
                alert('Wizard Completed');
            },
            onChange: (currentIndex, newIndex, stepDirection) => {
                if (currentIndex === 0 && stepDirection === "forward") {
                    if (this.cls.length == 0) {
                        new Vue().$message({
                            message: 'Please select at least one classification!',
                            type: 'warning',
                            offset: 70,
                        });
                        return false;
                    }
                    else if ($("#nameInput").val().trim() == "") {
                        new Vue().$message({
                            message: 'Please enter name!',
                            type: 'warning',
                            offset: 70,
                        });
                        return false;
                    } else if ($("#descInput").val().trim() == "") {
                        new Vue().$message({
                            message: 'Please enter overview!',
                            type: 'warning',
                            offset: 70,
                        });
                        return false;
                    }

                    if(this.currentLocalization.name === ""){
                        this.currentLocalization.name = $("#nameInput").val();
                    }

                    if(this.draft.oid!='')
                        this.createDraft();
                    return true;

                } else {
                    return true;
                }
            }
        });

        //判断是否登录
        $.ajax({
            type: "GET",
            url: "/user/load",
            data: {},
            cache: false,
            async: false,
            success: (data) => {
                console.log(data);
                if (data.oid == "") {
                    this.$alert('Please login first!', 'Error', {
                        type: "error",
                        confirmButtonText: 'OK',
                        callback: action => {
                            window.location.href = "/user/login";
                        }
                    });
                }
                else {
                    this.userId = data.oid;
                    this.userName = data.name;

                    this.sendUserToParent(this.userId)
                }
            }
        })

        let height = document.documentElement.clientHeight;
        this.ScreenMaxHeight = (height) + "px";
        this.IframeHeight = (height - 20) + "px";

        window.onresize = () => {
            console.log('come on ..');
            height = document.documentElement.clientHeight;
            this.ScreenMaxHeight = (height) + "px";
            this.IframeHeight = (height - 20) + "px";
        }

        var spatialObj = {};

        $(".finish").click(() => {
            let loading = this.$loading({
                lock: true,
                text: "Uploading...",
                spinner: "el-icon-loading",
                background: "rgba(0, 0, 0, 0.7)"
            });

            if(this.editType=='modify') {
                this.changeLocalization(this.currentLocalization);
            }
            for(i=0;i<this.localizationList.length;i++){
                let local = this.localizationList[i];
                if(local.localName.trim()==""||local.localCode.trim()==""){
                    this.$alert('<b>'+local.localName+'</b> localized name or description has not been filled in, please fill it or delete the localization language.', 'Warning', {
                        confirmButtonText: 'OK',
                        type: 'warning',
                        dangerouslyUseHTMLString: true,
                        callback: action => {

                        }
                    });
                    return;
                }
            }

            spatialObj = this.getItemContent('finish')

            let formData = new FormData();
            if ((oid === "0") || (oid === "") || (oid == null)) {

                let file = new File([JSON.stringify(spatialObj)], 'ant.txt', {
                    type: 'text/plain',
                });
                formData.append("info", file)
                $.ajax({
                    url: "/spatialReference/",
                    type: "POST",
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true,
                    data: formData,
                    success: (result) => {
                        loading.close();
                        if (result.code == "0") {
                            this.deleteDraft()
                            this.$confirm('<div style=\'font-size: 18px\'>Create spatiotemporal reference successfully!</div>', 'Tip', {
                                dangerouslyUseHTMLString: true,
                                confirmButtonText: 'View',
                                cancelButtonText: 'Go Back',
                                cancelButtonClass: 'fontsize-15',
                                confirmButtonClass: 'fontsize-15',
                                type: 'success',
                                center: true,
                                showClose: false,
                            }).then(() => {
                                window.location.href = "/repository/spatialReference/" + result.data;
                            }).catch(() => {
                                window.location.href = "/user/userSpace#/communities/spatialReference";
                            });
                        }
                        else if (result.code == -1) {
                            this.$alert('Please login first!', 'Error', {
                                type: "error",
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.href = "/user/login";
                                }
                            });
                        }
                        else {
                            this.$alert('Created failed!', 'Error', {
                                type: "error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                        }
                    }
                })
            }
            else {
                spatialObj["oid"] = oid;
                let file = new File([JSON.stringify(spatialObj)], 'ant.txt', {
                    type: 'text/plain',
                });
                formData.append("info", file)
                $.ajax({
                    url: "/spatialReference/"+oid,
                    type: "PUT",
                    cache: false,
                    processData: false,
                    contentType: false,
                    async: true,
                    data: formData,

                    success: (result) => {
                        loading.close();
                        if (result.code === 0) {
                            if (result.data.method === "update") {
                                this.deleteDraft()
                                this.$confirm('<div style=\'font-size: 18px\'>Update spatiotemporal reference successfully!</div>', 'Tip', {
                                    dangerouslyUseHTMLString: true,
                                    confirmButtonText: 'View',
                                    cancelButtonText: 'Go Back',
                                    cancelButtonClass: 'fontsize-15',
                                    confirmButtonClass: 'fontsize-15',
                                    type: 'success',
                                    center: true,
                                    showClose: false,
                                }).then(() => {
                                    window.location.href = "/repository/spatialReference/" + result.data.oid;
                                }).catch(() => {
                                    window.location.href = "/user/userSpace#/communities/spatialReference";
                                });
                            }
                            else {
                                let currentUrl = window.location.href;
                                let index = currentUrl.lastIndexOf("\/");
                                that.spatialReference_oid = currentUrl.substring(index + 1, currentUrl.length);
                                console.log(that.spatialReference_oid);
                                //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
                                // that.getnoticeNum(that.spatialReference_oid);
                                // let params = that.message_num_socket;
                                // that.send(params);
                                this.$alert('Changes have been submitted, please wait for the author to review.', 'Success', {
                                    type: "success",
                                    confirmButtonText: 'OK',
                                    callback: action => {
                                        window.location.href = "/user/userSpace";
                                    }
                                });

                            }

                        }
                        else if (result.code == -2) {
                            this.$alert('Please login first!', 'Error', {
                                type: "error",
                                confirmButtonText: 'OK',
                                callback: action => {
                                    window.location.href = "/user/login";
                                }
                            });
                        }
                        else {
                            this.$alert(result.msg, 'Error', {
                                type: "error",
                                confirmButtonText: 'OK',
                                callback: action => {

                                }
                            });
                        }

                    }
                })
            }
        });

        // $(".prev").click(()=>{
        //     let currentUrl = window.location.href;
        //     let index = currentUrl.lastIndexOf("\/");
        //     that.spatialReference_oid = currentUrl.substring(index + 1,currentUrl.length);
        //     console.log(that.spatialReference_oid);
        //     //当change submitted时，其实数据库中已经更改了，但是对于消息数目来说还没有及时改变，所以在此处获取消息数目，实时更新导航栏消息数目，
        //     that.getnoticeNum(that.spatialReference_oid);
        //     let params = that.message_num_socket;
        //     that.send(params);
        // });

        const timer = setInterval(()=>{
            if(this.itemName!=''&&this.startDraft==1){
                this.createDraft()
            }
        },30000)


        this.$once('hook:beforeDestroy', ()=>{
            clearInterval(timer)
            clearTimeout(this.timeOut)
        })

        $(document).on("keyup", ".username", function () {

            if ($(this).val()) {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html($(this).val());
            }
            else {
                $(this).parents('.panel').eq(0).children('.panel-heading').children().children().html("NEW");
            }
        })

    }
})