# activiti标准接口
1. 获取模型：/modeler.html?modelId=ACT_RE_MODEL.ID_
2. 添加模型：/model/create
3. 保存模型：/model/{ACT_RE_MODEL.ID_}/save
4. 发布模型,每发布一次就产生一个新的流程：/model/deploy/{ACT_RE_MODEL.ID_}
5. 运行中的流程: /model/getRunningProcess
6. 通过流程ID启动一个流程实例：/model/instance-create/{ACT_RE_PROCDEF.ID_}
7. 通过流程ID获取节点：/model/getProcessNode/{ACT_RE_PROCDEF.ID_}
8. 通过流程ID获取第一个节点：/model/getFirstNode/{ACT_RE_PROCDEF.ID_}
9. 通过流程的实例ID获取下一个节点：/model/getNextNode/{ACT_RU_TASK.PROC_INST_ID_}
10. 高亮输出流程实例的流程图：/model/getHighlightImg/{ACT_RU_TASK.PROC_INST_ID_}
11. 通过某个任务：/model/pass/{流程实例id}/{任务id}
12. 驳回某个任务：/model/back/{流程实例id}/{任务id}
13. 删除任务：/model/delete/{任务id}
14. 删除任务的历史：/model/deleteHistoric/{任务id}
15. 删除运行中的实例：/model/delInsByIds/{流程实例id}
# 数据流向
## /model/create ACT_RE_MODEL,ACT_GE_BYTEARRAY
1. ACT_RE_MODEL.EDITOR_SOURCE_VALUE_ID_和ACT_RE_MODEL.EDITOR_SOURCE_EXTRA_VALUE_ID_在ACT_GE_BYTEARRAY对应两条记录
2. ACT_RE_MODEL表里EDITOR_SOURCE_VALUE_ID_对应ACT_GE_BYTEARRAY的ID_，表示该模型对应的模型文件（json格式数据） 
3. ACT_RE_MODEL表里EDITOR_SOURCE_EXTRA_VALUE_ID_对应ACT_GE_BYTEARRAY的ID_，表示该模型生成的图片文件
## /model/1/save ACT_RE_MODEL
更新ACT_RE_MODEL里对应的name,key,description等值
## /model/deploy/1 ACT_RE_DEPLOYMENT,ACT_RE_PROCDEF
当模型发布之后，将会产生一个流程，而下次发布时，还会产生一个新的，所以模型与流程是一对多的关系；而对于流程的部署表也是一样，每发布
一次，在部署表里也会多一条记录，流程表里会记录部署表的ID_
1. ACT_RE_DEPLOYMENT添加数据
2. ACT_RE_PROCDEF添加数据
3. ACT_GE_BYTEARRAY里的DEPLOYMENT_ID_对应ACT_RE_DEPLOYMENT的ID_
## 启动流程
> 模型---发布---流程---启动---流程实例---任务---步骤审核---进入ACT_HI_TASKINST
* ACT_RU_EXECUTION 流程实例表
* ACT_RU_TASK 流程运行任务表
每次启动一个流程，都对对应一个新的流程实例（ACT_RU_EXECUTION）和多条任务（ACT_RU_TASK），即ACT_RU_EXECUTION和ACT_RE_PROCDEF添加数据
是多对一的关系
## 流程节点权限
ACT_RU_EXECUTION.ACT_ID_：流程实例中节点的节点ID，以后权限权限主要根据这个字段