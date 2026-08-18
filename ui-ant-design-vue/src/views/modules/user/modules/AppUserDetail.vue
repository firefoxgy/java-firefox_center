<template>
  <a-modal
    :title="title"
    :width="modalWidth"
    :visible="visible"
    :destroyOnClose="true"
    :maskClosable	="true"
    :footer="null"
    @cancel="handleCancel"
    cancelText="关闭">

    <template slot="title">
      <div style="width: 100%;">
        <span>{{ title }}</span>
      </div>
    </template>
      <a-descriptions bordered size="small">        
        <a-descriptions-item label="header_img" :span="3" style="word-break: break-all">
          <img :src=model.headerImg width="200"/>
          </a-descriptions-item>  
        <a-descriptions-item label="应用id" >{{ model.appId }}</a-descriptions-item>
        <a-descriptions-item label="租户id" :span="2">{{ model.tenantId }}</a-descriptions-item>
        <a-descriptions-item label="uid" >{{ model.uid }}</a-descriptions-item>
        <a-descriptions-item label="phone" :span="2">{{ model.phone }}</a-descriptions-item>
        <a-descriptions-item label="open_int_id" >{{ model.openIntId }}</a-descriptions-item>
        <a-descriptions-item label="open_id" :span="2">{{ model.openId }}</a-descriptions-item>
        <a-descriptions-item label="username" >{{ model.username }}</a-descriptions-item>
        <a-descriptions-item label="nickname" >{{ model.nickname }}</a-descriptions-item>
        <a-descriptions-item label="gender" :span="3">{{ model.gender }}</a-descriptions-item>        
        <a-descriptions-item label="email">{{ model.email }}</a-descriptions-item>
        <a-descriptions-item label="密码加密方式" :span="2">
          <span v-if="model.status==-1">oauth</span>
          <span v-else>md5</span>
        </a-descriptions-item>      
        <a-descriptions-item label="version">{{ model.version }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ model.createTime }}</a-descriptions-item>
        <a-descriptions-item label="状态" >
          <a-tag v-if="model.status==-1" color="red">已删除</a-tag>
          <a-tag v-if="model.status==0" color="yellow">禁用</a-tag>
          <a-tag v-else color="green">启用</a-tag>
        </a-descriptions-item>
      </a-descriptions>
      <br />
      <b>绑定的第三方帐号</b>
      <br />
      <div>
        <a-table
          size="small"
          bordered
          rowKey="id"
          :columns="columns1"
          :dataSource="dataSource1"
          :pagination="ipagination"
          :loading="loading"
          :scroll="{ y: 480 }"
          @change="handleTableChange">

          <span slot="action" slot-scope="text, record">
            <a @click="handleThirdView(record)" >详情</a>
          </span>

          <!-- 状态渲染模板 -->
          <template slot="customRenderState" slot-scope="relStatus">
            <a-tag v-if="relStatus==0" color="#dd4b39">禁用</a-tag>
            <a-tag v-if="relStatus==1" color="#3c8dbc">正常</a-tag>          
          </template>         

        </a-table>
      </div>
      <!-- table区域-end -->
      <third-user-detail ref="modalThirdDetailForm"></third-user-detail>
  </a-modal>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import { getAction } from '@/api/manage'
  import ThirdUserDetail from './ThirdUserDetail'

  export default {
    name: "user_AppUserDetail",
    components: {
      ThirdUserDetail
    },
    data () {
      return {
        title:"详情",
        visible: false,
        modalWidth:1080,
        model: {},
        qrcode: '',
        thirdList: {},
        queryParam: {
          appid: '',
        },
        // 表头
        columns1: [
          {
            title:'login_type',
            align:"left",
            dataIndex: 'loginType'
          },
          {
            title:'sid',
            align:"left",
            dataIndex: 'sid'
          },
          {
            title:'nickname',
            align:"left",
            dataIndex: 'nickname'
          },
          {
            title:'email',
            align:"left",
            dataIndex: 'email'
          },
          {
            title:'gender',
            align:"left",
            dataIndex: 'gender'
          },
          {
            title:'reg_from',
            align:"left",
            dataIndex: 'regFrom'
          },
          {
            title: '状态',
            align: "center",
            dataIndex: 'relStatus',
            scopedSlots: { customRender: 'customRenderState' },
            filterMultiple: false,
            filters: [
              { text: '正常', value: 1 },
              { text: '禁用', value: 0 },
            ]
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: {customRender: 'action'},
            align: "center"
          }
        ],
        //数据集
        dataSource1: [],
        // 分页参数
        ipagination: {
          current: 1,
          pageSize: 20,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc',
        },
        loading: false,
        selectedRowKeys: [],
        selectedRows: [],
        url:{
          thirdList: '/sys/user/app/thirdList'
        }
      }
    },
    created () {

    },
    computed:{

    },
    methods: {
      view (record) {
        this.model={}     
        this.model=record;
        this.visible = true;
        this.initThirdList();
      },
      initThirdList(arg){
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件        
        getAction(this.url.thirdList, params).then((res) => {
          if (res.success) {
            this.dataSource1 = res.data.records;
            this.ipagination.total = res.data.total;
          }
        })
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter);
        param.appId=this.model.appId;
        param.tenantId=this.model.tenantId;
        param.uid=this.model.uid;
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      getQueryField() {
        //TODO 字段权限控制
      },
      handleTableChange(pagination, filters, sorter) {
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.initThirdList();
      },
      handleThirdView(record){
        this.$refs.modalThirdDetailForm.view(record);
        this.$refs.modalThirdDetailForm.title="详情";
        this.$refs.modalThirdDetailForm.disableSubmit = true;
      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.disableSubmit = false;
      },
      handleOk () {
        this.close()
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td{
    padding-top:10px;
    padding-bottom:10px;
  }

  .drawer-bootom-button {
    position: absolute;
    bottom: -8px;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }
</style>