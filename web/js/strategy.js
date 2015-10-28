$(function(){

    getDocument();
});
function getDocument(){
    var data={
        author:123,
        page:1
    };
    ajaxRequest('/getDocumentList',data,function(){

    });
}