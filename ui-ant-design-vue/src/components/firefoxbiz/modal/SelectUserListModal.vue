<template>
  <a-modal
    title="用户列表"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel">

    <a-table
      ref="table"
      bordered
      size="middle"
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"></a-table>
  </a-modal>
</template>

<script>
  import {getUserList} from '@/api/api'
  import {FirefoxListMixin} from '@/mixins/FirefoxListMixin'

  export default {
    name: "SelectUserListModal",
    mixins: [FirefoxListMixin],
    data() {
      return {
        title: "操作",
        visible: false,
        model: {},
        confirmLoading: false,
        url: {
          add: "/act/model/create",
          list: "/sys/user/list"
        },
        columns: [
          {
            title: '用户账号',
            align: "center",
            dataIndex: 'username',
            fixed: 'left',
            width: 200
          },
          {
            title: '用户姓名',
            align: "center",
            dataIndex: 'realname',
          },
          {
            title: '性别',
            align: "center",
            dataIndex: 'sex_dictText'
          },
          {
            title: '手机号码',
            align: "center",
            dataIndex: 'phone'
          },
          {
            title: '邮箱',
            align: "center",
            dataIndex: 'email'
          },
          {
            title: '状态',
            align: "center",
            dataIndex: 'status_dictText'
          }
        ]
      }
    },
    created() {
      //Step.2 加载用户数据
      getUserList().then((res) => {
        if (res.success) {
          this.dataSource = res.data.records;
          this.ipagination.total = res.data.total;
        }
      })
    },
    methods: {
      open() {
        this.visible = true;

        //Step.1 清空选中用户
        this.selectedRowKeys = []
        this.selectedRows = []
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      handleChange(info) {
        let file = info.file;
        if (file.response.success) {
          this.$message.success(file.response.msg);
          this.$emit('ok');
          this.close()
        } else {
          this.$message.warn(file.response.msg);
          this.close()
        }

      },
      handleCancel() {
        this.close()
      },
      handleSubmit() {
        this.$emit('ok', this.selectionRows);
        this.close()
      },
    }
  }
</script>

<style>

</style>
