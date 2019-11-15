# activiti标准接口
1. 获取模型：/modeler.html?modelId=ACT_RE_MODEL.ID_
2. 添加模型：/model/create
3. 保存模型：/model/{ACT_RE_MODEL.ID_}/save
4. 发布模型：/model/deploy/{ACT_RE_MODEL.ID_}
5. 运行中的流程: /model/getRunningProcess
6. 启动一个流程实现：/model/instance-create/{ACT_RE_PROCDEF.ID_}
7. 获取节点：/model/getProcessNode/{ACT_RE_PROCDEF.ID_}
8. 获取第一个节点：/model/getFirstNode/{ACT_RE_PROCDEF.ID_}
9. 获取下一个节点：/model/getNextNode/{ACT_RU_TASK.PROC_INST_ID_}

# 数据流向
## /model/create ACT_RE_MODEL,ACT_GE_BYTEARRAY
1. ACT_RE_MODEL.EDITOR_SOURCE_VALUE_ID_和ACT_RE_MODEL.EDITOR_SOURCE_EXTRA_VALUE_ID_在ACT_GE_BYTEARRAY对应两条记录
2. ACT_RE_MODEL表里EDITOR_SOURCE_VALUE_ID_对应ACT_GE_BYTEARRAY的ID_，表示该模型对应的模型文件（json格式数据） 
3. ACT_RE_MODEL表里EDITOR_SOURCE_EXTRA_VALUE_ID_对应ACT_GE_BYTEARRAY的ID_，表示该模型生成的图片文件
## /model/1/save ACT_RE_MODEL
更新ACT_RE_MODEL里对应的name,key,description等值
## /model/deploy/1 ACT_RE_DEPLOYMENT,ACT_RE_PROCDEF
1. ACT_RE_DEPLOYMENT添加数据
2. ACT_RE_PROCDEF添加数据
3. ACT_GE_BYTEARRAY里的DEPLOYMENT_ID_对应ACT_RE_DEPLOYMENT的ID_