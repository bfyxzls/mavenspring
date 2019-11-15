# ACT_GE_BYTEARRAY
> 二进制数据表，存储通用的流程定义和流程资源。（act_ge_bytearray）

保存流程定义图片和xml、Serializable(序列化)的变量,即保存所有二进制数据，特别注意类路径部署时候，不要把svn等隐藏文件或者其他
与流程无关的文件也一起部署到该表中，会造成一些错误（可能导致流程定义无法删除）

# ACT_GE_PROPERTY
> 属性数据表(act_ge_property)

属性数据表。存储整个流程引擎级别的数据。

# ACT_HI_ACTINST
> 历史节点表（act_hi_actinst）

历史活动信息。这里记录流程流转过的所有节点，与HI_TASKINST不同的是，taskinst只记录usertask内容

# ACT_HI_ATTACHMENT
> 历史附件表( act_hi_attachment )

# ACT_HI_COMMENT
> 历史意见表( act_hi_comment )

# ACT_HI_DETAIL
> 历史详情表( act_hi_detail )

# ACT_HI_IDENTITYLINK
> 历史流程人员表( act_ru_identitylink )  

任务参与者数据表。主要存储历史节点参与者的信息

# ACT_HI_PROCINST
> 历史流程实例表（act_hi_procinst）

# ACT_HI_TASKINST
> 历史任务实例表( act_hi_taskinst )

# ACT_HI_VARINST
> 历史变量表( act_hi_varinst )

# ACT_ID_GROUP
> 用户组信息表( act_id_group )

# ACT_ID_INFO
> 用户扩展信息表( act_id_info )

# ACT_ID_MEMBERSHIP
> 用户与分组对应信息表( act_id_membership )

# ACT_ID_USER
> 用户信息表( act_id_user )

# ACT_RE_DEPLOYMENT
> 部署信息表( act_re_deployment )

# ACT_RE_MODEL
> 流程设计模型部署表( act_re_model )

# ACT_RE_PROCDEF
> 流程定义数据表( act_re_procdef )

业务流程定义数据表。此表和 ACT_RE_DEPLOYMENT 是多对一的关系，即，一个部署的bar包里可能包含多个流程定义文件，
每个流程定义文件都会有一条记录在 ACT_REPROCDEF 表内，每个流程定义的数据，都会对于 ACT_GE_BYTEARRAY 表内的一个
资源文件和 PNG 图片文件。和 ACT_GE_BYTEARRAY 的关联是通过程序用ACT_GE_BYTEARRAY.NAME 与 ACT_RE_PROCDEF.NAME 完成的，
在数据库表结构中没有体现。

# ACT_RU_EVENT_SUBSCR
> 事实订阅表（act_ru_event_subscr）

# ACT_RU_EXECUTION
> 运行时流程执行实例表( act_ru_execution )

# ACT_RU_IDENTITYLINK
> 运行时流程人员表( act_ru_identitylink )

任务参与者数据表。主要存储当前节点参与者的信息。

# ACT_RU_JOB
> 运行时定时任务数据表( act_ru_job )

# ACT_RU_TASK
> 运行时任务节点表( act_ru_task )

# ACT_RU_VARIABLE
> 运行时流程变量数据表( act_ru_variable )