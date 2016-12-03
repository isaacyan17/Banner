## Mac下生成多个SSH KEY并管理

* 查找已经存在的SSH KEY
```
ls -al ~/.ssh
```

* 生成一个key
```
ssh-keygen -t rsa -C "your_email@example.com"
# Creates a new ssh key using the provided email
Generating public/private rsa key pair.
Enter file in which to save the key (/your_home_path/.ssh/id_rsa):
```
<font color=Tomato >
这里注意如果以前已经生成过sshkey，文件名为"id_rsa"，如果不想覆盖的话需要在后面输入新的文件名,如:"id_rsa2"
</font>


```
Enter passphrase (empty for no passphrase): [Type a passphrase]
Enter same passphrase again: [Type passphrase again]
```
如果需要设置密码则自己填入

* 将key加入到ssh-agent中
```
$ ssh-add -l
Could not open a connection to your authentication agent.
如果发现上面的提示,说明系统代理里没有任何key,执行如下操作
exec ssh-agent bash
```
如果系统已经有ssh-key 代理 ，将.ssh目录下的密钥添加到ssh-agent中
```
$ ssh-add ~/.ssh/id_rsa
$ ssh-add ~/.ssh/id_rsa2
......
```

* 在.ssh中创建config配置文件
```
nano ~/.ssh/config
```
或者可以直接创建config文件，再打开编辑
```
open ~/.ssh/config
```

* 输入配置信息
```
#id_rsa (某github 配置)
Host git@github.com:xxxxx
    HostName git@github.com:xxxxx
    User git
    IdentityFile ~/.ssh/id_rsa
    
 #id_rsa2 (另一个github配置)
    HostName git@github.com:xxxxx
    User git
    dentityFile ~/.ssh/id_rsa2
 ...
```

多个SSH帐号配置完成以后，如果需要改动某工程的`.git`文件中的远程url，`cd ~/xxx/.git`打开`config`文件，将`remote origin `的url前段修改为你设置的Hostname即可。
如 `url = git@github.com:xxxxx/xxx.git`