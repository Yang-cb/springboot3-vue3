<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";
import {useCounterStore} from "@/stores/counter";

// 表单信息
const form = reactive({
    username: '',
    password: '',
    remember: false // 记住我选项是否勾选
})

const store = useCounterStore()

const login = () => {
    if (!form.username || !form.password) {
        ElMessage.warning('用户名或密码为空')
    }
    post('/api/auth/login', {
        username: form.username,
        password: form.password,
        remember: form.remember
    }, (data) => {
        ElMessage.success(data)
        get('/api/user/detail', (data) => {
            store.auth.user = data
            router.push('/index')
        }, () => {
            store.auth.user = null
        })
    })
}
</script>

<template>
    <div style="text-align: center;margin: 0 20px;">
        <div style="margin-top:150px">
            <div style="font-size: 25px;font-weight: bolder">登录</div>
            <div style="font-size: 10px;color: grey">继续，即表示您的帐户同意我们的用户协议和隐私政策。
            </div>
        </div>

        <div style="margin-top: 30px">
            <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
                <template #prefix>
                    <el-icon>
                        <User/>
                    </el-icon>
                </template>
            </el-input>
            <el-input v-model="form.password" type="password" style="margin-top: 10px" placeholder="密码">
                <template #prefix>
                    <el-icon>
                        <Lock/>
                    </el-icon>
                </template>
            </el-input>
        </div>

        <el-row style="margin-top: 5px">
            <el-col :span="12" style="text-align: left">
                <el-checkbox v-model="form.remember" label="记住我"/>
            </el-col>
            <el-col :span="12" style="text-align: right">
                <el-link @click="router.push('/resetPassword')">忘记密码</el-link>
            </el-col>
        </el-row>

        <div style="margin-top: 30px">
            <el-button @click="login" style="width: 270px" type="success" plain>立即登录</el-button>
        </div>

        <el-divider>
            <span style="font-size: 10px;color: gray">没有账号</span>
        </el-divider>

        <div>
            <el-button @click="router.push('/register')" style="width: 270px" type="warning" plain>前往注册</el-button>
        </div>
    </div>
</template>

<style scoped>

</style>