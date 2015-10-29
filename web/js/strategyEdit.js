/**
 * Created by Administrator on 2015/10/20.
 */

$(function(){
    if(window.username&&window.password){
        markdown();
        $('.nickName').html(decodeURIComponent(window.username));
        $('.title').on('click','a',function(){
            $('.selectedType').html($(this).html());
            $('.title li').removeClass('active');
            $(this).parent().addClass('active');
        });
    }else{
        location.href='login.html';
    }

});
function markdown(){
    var opts = {
        container: 'epiceditor',
        textarea: null,
        basePath: 'epiceditor',
        clientSideStorage: true,
        localStorageName: 'epiceditor',
        useNativeFullscreen: true,
        parser: marked,
        file: {
            name: 'epiceditor',
            defaultContent: '',
            autoSave: 100
        },
        theme: {
            base: '../../../libs/EpicEditor/themes/base/epiceditor.css',
            preview: '../../../libs/EpicEditor/themes/preview/github.css',
            editor: '../../../libs/EpicEditor/themes/editor/epic-light.css'
        },
        button: {
            preview: true,
            fullscreen: true,
            bar: "auto"
        },
        focusOnLoad: false,
        shortcut: {
            modifier: 18,
            fullscreen: 70,
            preview: 80
        },
        string: {
            togglePreview: 'Toggle Preview Mode',
            toggleEdit: 'Toggle Edit Mode',
            toggleFullscreen: 'Enter Fullscreen'
        },
        autogrow:{
            minHeight:300,
            maxHeight:500,
            scroll:true
        }
    };
    var editor = new EpicEditor(opts).load();
    var getEditor=editor.getElement('editor').body;
    $(getEditor).empty();
    $('.submit').click(function () {
        var sendVal=editor.getElement('previewer').body.innerHTML;
        var preview=encodeURIComponent(sendVal.slice(0,99));
        var title=encodeURIComponent($('.blog-title input').val());
        var type=$('.title .active').attr('data-type');
        addDocument(type,title,encodeURIComponent(sendVal),preview);
    });

    boldText(getEditor);
    italicText(getEditor);
    headerText(getEditor);
    addLink(getEditor);
    uploadImage($('.upload'),getEditor);
    addCode(getEditor);
}

//粗体
function boldText(getEditor){
    $('.su-tool-bold').click(function(){
        $(getEditor).append('****');
    });
}

//斜体
function italicText(getEditor){
    $('.su-tool-italic').click(function(){
        $(getEditor).append('**');
    });
}

//标题
function headerText(getEditor){
    $('.su-tool-head').click(function(){
        $(getEditor).append('###');
    });
}

//添加链接
function addLink(getEditor){
    $('.confirm').click(function(){
        var linkAddress=$('.linkAddress').val();
        var linkFont=$('.linkFont').val();
        $(getEditor).append('['+linkFont+'](http://'+linkAddress+')');
    });
}

//添加代码
function addCode(getEditor){
    $('.su-tool-code').click(function(){
        $(getEditor).append('``');
    });
}

function addDocument(type,title,body,preview){
    var data={
        type:type,
        title:title,
        body:body,
        preview:preview.length
    };
    ajaxHeader('/addDocument',data,function(data){
        //location.href='index.html';
    });
}


//上传图片
function uploadImage(target,getEditor){
    target.change(function(event){
        var file=this.files[0];
        if(!file)  return null;
        var rFilter=/^(image\/bmp|image\/gif|image\/jpeg|image\/png|image\/tiff)$/i;
        if(!checkUploadAccess()){
            alert('没有上传图片权限');
            return null;
        }else{
            if(!rFilter.test(file.type)){
                alert('请选择图片');
                return null;
            }
        }
        uploadImg(file,getEditor);
    });
}

function uploadImg(file,getEditor) {
    //var formData = new FormData($('form')[0]);
    //formData.append('img', file);
    var xhr=false;
    var fileType=(file.type).toString().slice(6);
    try {
        xhr = new XMLHttpRequest();
    } catch(e) {
        try {
            xhr = new ActiveXObject('Msxml2.XMLHTTP');
        } catch (e) {
            xhr = new ActiveXObject('Microsoft.XMLHTTP');
        }
    }
    xhr.open('POST', '/uploadImage');
    xhr.setRequestHeader('username',window.username);
    xhr.setRequestHeader('password',window.password);
    xhr.setRequestHeader('File-Type',fileType);
    xhr.send(file);
    xhr.onreadystatechange = function(e) {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var data=JSON.parse(e.target.response);
            var hostName=window.location.hostname;
            var fileName=data.data.return;
            $(getEditor).append('![Alt text](http://'+hostName+':8001/'+fileName+')');
        }
    };
}
function checkUploadAccess(){
    ajaxHeader('/checkUploadAccess',null,function(data){
    });
    return true;
}



