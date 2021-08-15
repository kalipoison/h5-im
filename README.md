h5-im

初衷：网上有许多IM的第三方SDK，如：融云、环信、腾讯TIM。
如果用第三方的SDK，意味着消息数据需要存储到第三方的服务器上，再者，可扩展性、灵活性肯定没有自己开发的要好，还有一个小问题，就是收费。比如，融云免费版只支持100个注册用户，超过100就要收费，群聊支持人数有限制等等...



技术栈： 

​	后端： springboot + netty + docker + fastdfs

​	前端： mui



功能：

​	登录注册功能、头像页面功能，昵称页面功能，二维码生成功能，好友搜索，好友请求处理，扫一扫功能，通讯录功能，以及聊天功能（消息快照，已读、未读、离线处理）。