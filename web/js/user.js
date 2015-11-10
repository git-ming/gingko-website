/**
 * Created by Administrator on 2015/10/29.
 */
$(function(){
    $('.nickName').html(decodeURIComponent(sessionStorage.username));

    $('.relation').click(function(){
        getMarkedList();
        getMarkedMeList();
    });

    $('.messages').click(function(){
        getMessageList();

        //ȫ�����Ϊ�Ѷ�
        $('.readAll').click(function(){
            var data = JSON.parse(sessionStorage.messageList);
            ajaxHeader('/readAllMessage',data,function(data){
                $('.message-list li').css('opacity', '0.6').attr('data-target','');
                $('.messageNum').html('0');
            });
        });

        //����Ѷ�
        $('.message-list').on('click','li',function(){
            var _this=$(this);
            if($(this).css('opacity')==1){
                readMessage(_this);
                getMessage($(this).attr('messageId'));
            }
        }).on('click','input',function(event){   //ѡ��
            event.stopPropagation();
        });

        $('.delete').click(function(){
            var selectArr=[];
            var messageLi=$('.message-list li');
            for(var i=0;i<messageLi.length;i++){
                var isSelected=messageLi.eq(i).find('input').prop('checked');
                if(isSelected){
                    selectArr.push({
                        id:messageLi.eq(i).attr('messageId')
                    });
                }
            }
            removeMessage(selectArr);
        });
    });

});


//��ȡ��Ϣ�б�
function getMessageList(){
    ajaxHeader('/getMessageList',null,function(data){
        var messageNum=0;
        var array=[];
        var newData=rankByTime(data);
        for(var i=0;i<newData.length;i++){
            $('.message-list').prepend($('#message-template').html());
            var content=decodeURIComponent(newData[i].preview)||'��Ǹ����������';
            var author=decodeURIComponent(newData[i].author)||'admin';
            var date=transformDate(newData[i].time.time);
            var type=newData[i].type||'ϵͳ';

            var messageNo=$('.message-list li').eq(0);
            messageNo.find('.message-tag').html(type+':');
            messageNo.find('.message-content').html(content);
            messageNo.find('.message-author').html('by'+author);
            messageNo.find('.message-date').html(date);
            messageNo.attr('messageId',newData[i].id);


            if(newData[i].read){
                messageNo.css('opacity','0.6').attr('data-target','');
            }else{
                messageNum+=1;
            }
        }
        sessionStorage.messageList=JSON.stringify(newData);
        $('.messageNum').html(messageNum);
    });
}

//��ע�����û��б�
function getMarkedList(){
    var data={
        username:sessionStorage.username
    };
    ajaxRequest('/getMarkedList',data,function(response){
        $('.focus dt').html(response.length);
        $('.focusNum').html(response.length);
        $('.focus-box .list').empty();
        for(var i=0;i<response.length;i++){
            $('.focus-box .list').append($('#list').html());
            var target=$('.focus-box .list>a').eq(i);
            var name=decodeURIComponent(response[i].to);
            var headImgPath=response[i].img||'../images/logo.png';

            $(target).attr('href','space.html?user='+name);
            $(target).find('img').attr('src',headImgPath);
            $(target).find('.fansName').html(name);
        }
    });
}

//�������û���ע�б�
function getMarkedMeList(){
    var data={
        username:sessionStorage.username
    };
    ajaxRequest('/getMarkedMeList',data,function(response){
        $('.fans dt').html(response.length);
        $('.fansNum').html(response.length);
        $('.fans-box .list').empty();
        for(var i=0;i<response.length;i++){
            $('.fans-box .list').append($('#list').html());
            var target=$('.fans-box .list>a').eq(i);
            var name=decodeURIComponent(response[i].from);
            var headImgPath=response[i].img||'../images/logo.png';

            $(target).attr('href','space.html?user='+name);
            $(target).find('img').attr('src',headImgPath);
            $(target).find('.fansName').html(name);
        }
    });
}


//��ȡ��Ϣ
function readMessage(_this){
    var data={
        id:$(_this).attr('messageId')
    };
    ajaxHeader('/readMessage',data,function(data){
        var messageNum=$('.messageNum');
        $(_this).css('opacity','0.6').attr('data-target','');
    });
}

//ɾ����Ϣ
function removeMessage(sendData){
    ajaxHeader('/removeAllMessage',sendData,function(data){
        var messageLi=$('.message-list li');
        var length=messageLi.length;
        for(var i=0;i<length;i++){
            var isSelected=messageLi.eq(i).find('input').prop('checked');
            if(isSelected){
                messageLi.eq(i).remove();
            }
        }
    });
}

//��ϸ��Ϣ
function getMessage(id){
    var data={
        id:id
    };
    ajaxHeader('/getMessage',data,function(data){
        var info=$('#messageModal');
        var message=decodeURIComponent(data.message);
        var author=decodeURIComponent(data.author);
        $(info).find('.modal-title').html(data.type+'<span class="label label-default">by'+author+'</span>');
        $(info).find('.modal-body').html('<p>'+message+'</p>');
    });
}

//������Ϣ�б�
function traverseMessage(){
    var messageData=JSON.parse(sessionStorage.messageList);
    var messageNum=0;
    for(var i=0;i<messageData.length;i++){
        /*var isSelected=messageData.eq(i).find('input').prop('checked');
        if(isSelected){
            messageData.eq(i).remove();
        }*/
        if(!messageData[i].read){
            messageNum+=1;
        }
    }
    $('.messageNum').html(messageNum);
}