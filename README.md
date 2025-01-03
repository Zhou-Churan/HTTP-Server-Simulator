# HTTP通信模拟器

---

1、项目结构

```bash
.
│  README.md
│  
│  .idea
│  out 
│  HTTP-Server-Simulator.iml
│
└─net
   │
   ├─client
   │   ├─Client.java     # 客户端启动文件
   │   ├─ResponseHandler.java     # 处理来自服务端的响应
   │   ├─1.txt     # 均为测试文件
   │   ├─2.html
   │   └─3.jpg
   │
   ├─server
   │   ├─Server.java     # 服务端启动文件
   │   ├─RequestHandler.java     # 处理来自客户端的请求
   │   ├─1.txt
   │   ├─2.html
   │   └─3.jpg
   │
   ├─message
   │   ├─Request.java     # 模拟请求对象
   │   └─Response.java     # 模拟响应对象
   │
   └─Login
       ├─loginClient    # 子类
       │    ├─LoginClient.java
       │    └─LoginResponseHandler.java
       │
       └─loginServer    # 子类
            ├─userData.txt    # 用户信息
            ├─LoginServer.java
            └─LoginRequestHandler.java
```



2、项目运行方式

- 先启动服务端，输入端口号
- 再启动客户端，输入远程连接的地址（本次实验中为 localhost 即可）和端口号
- 在客户端模拟向服务端发送请求
- 在服务端中查看收到的来自客户端的请求报文，并发送响应报文
- 在客户端查看来自服务端的响应报文



3、其他

对于响应报文的状态码，规定如下：首先输入文件名，之后选择是否设为 500 状态（服务器内部错误）；如果请求方式非GET和POST，则设为 405 状态（请求方法无法获取资源）；如果输入的文件名不存在，则设为 404 状态（找不到对应资源）；再之后选择是否进入 301 / 302 状态（永久重定向 / 临时重定向）；若以上选项都选择否，则以 200 状态发送响应报文。
解释：状态 500、301、302 需要手动选择是否进入，其他状态根据用户操作自动进入。



4、登录注册模块说明

- 该模块基于上述的模拟器实现，其所使用的类均继承模拟器相应的类
- 相比于基础模拟器，优化了操作难度，无需输入地址和端口号，且不设置异常状态，响应报文均以 200 状态码返回。用户只需在客户端进行登录注册操作，无需在服务端操作（服务端中可查看请求报文）。
- **注意**：千万不要轻易修改 ==userData.txt== 文件（可以查看，不可直接修改），如果不小心修改了文件，并导致无法正常登录，请将 userData.txt 文件中内容全部清空（所有换行符、空格也要删除），再重新进行注册操作。