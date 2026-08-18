<template>
  <div>
    <a-modal
      centered
      :title="title"
      :width="1000"
      :visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭">


      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline"  @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :span="4">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="handleAuth" icon="search">授权服务</a-button>                
              </span>
            </a-col>
            <a-col :span="8">
              <a-form-item label="服务名称">
                <a-input placeholder="请输入服务名称" v-model="queryParam.name"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              </span>
            </a-col>

          </a-row>
        </a-form>
      </div>
      <!-- table区域-begin -->
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
            <a @click="handleEdit(record)" >编辑</a>
          </span>

          <!-- 状态渲染模板 -->
          <template slot="customRenderState" slot-scope="relStatus">
            <a-tag v-if="relStatus==0" color="#dd4b39">禁用</a-tag>
            <a-tag v-if="relStatus==1" color="#3c8dbc">正常</a-tag>          
          </template>

        </a-table>
      </div>
      <!-- table区域-end -->
      <app-tenant-service-tree ref="modalAppTenantServiceTree" @ok="modalAppTenantServiceTreeOk"></app-tenant-service-tree>
      <app-tenant-service-status-modal ref="modalAppTenantServiceStatusForm" @ok="modalAppTenantServiceStatusFormOk"></app-tenant-service-status-modal>
    </a-modal>
  </div>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import {getAction} from '@/api/manage'  
  import AppTenantServiceTree from './AppTenantServiceTree'
  import AppTenantServiceStatusModal from './AppTenantServiceStatusModal'

  export default {
    name: "center_AppTenantServiceModal",
    components: {
      AppTenantServiceTree,
      AppTenantServiceStatusModal
    },
    data() {
      return {
        title: "服务",
        names: [],
        visible: false,
        placement: 'right',
        description: '',
        // 查询条件
        appId:'',
        tenantId:'',
        queryParam: {
          appId: '',
          tenantId:'',
        },
        // 表头
        columns1: [
          {
            title:'名称',
            align:"left",
            dataIndex: 'name'
          },
          {
            title:'服务名',
            align:"left",
            dataIndex: 'service'
          },
          {
            title:'路径',
            align:"left",
            dataIndex: 'path'
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
        url: {
          list: "/sys/center/app/servicePage",
        }
      }
    },
    created() {
      
    },
    methods: {
      show(appId, tenantId) {
        this.appId=appId;
        this.tenantId=tenantId;
        this.queryParam.appId=appId;
        this.queryParam.tenantId=tenantId;
        this.loadData();
        this.visible=true;
      },
      searchQuery() {
        this.loadData(1);
      },
      searchReset() {
        this.queryParam = {};
        this.queryParam.appId=this.appId;
        this.queryParam.tenantId=this.tenantId;
        this.loadData(1);
      },
      handleCancel() {
        this.visible = false;
      },
      handleOk() {
        this.dataSource2 = this.selectedRowKeys;
        this.$emit("selectFinished", this.dataSource2);
        this.visible = false;
      },
      loadData(arg) {
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        getAction(this.url.list, params).then((res) => {
          if (res.success) {
            this.dataSource1 = res.data.records;
            this.ipagination.total = res.data.total;
          }
        })
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter);
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
        this.loadData();
      },
      handleAuth: function () {
        this.$refs.modalAppTenantServiceTree.show(this.appId, this.tenantId);
        this.$refs.modalAppTenantServiceTree.disableSubmit = false;
      },
      handleEdit: function (record) {
        record.appId=this.appId;
        record.tenantId=this.tenantId;
        record.centerId=record.id;
        this.$refs.modalAppTenantServiceStatusForm.edit(record);
        this.$refs.modalAppTenantServiceStatusForm.title = "编辑";
        this.$refs.modalAppTenantServiceStatusForm.disableSubmit = false;
      },
      modalAppTenantServiceTreeOk(formData,arr){
        this.loadData()     
      },
      modalAppTenantServiceStatusFormOk(formData,arr){
        this.loadData()     
      }
    }
  }
</script>
<style lang="less" scoped>
  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }
</style>