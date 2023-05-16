<script setup>

import {ChatDotSquare, Lock, Message, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const form = reactive({
    username: '',
    password: '',
    password_repeat: '', //确认密码
    email: '',
    code: '' //用户填写的验证码
})

const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        //表示匹配一个字符，可以是英文字母（大小写不限）、数字或中文字符。
        callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
    } else {
        callback()
    }
}

const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== form.password) {
        callback(new Error("两次输入的密码不一致"))
    } else {
        callback()
    }
}

//电子邮件是否有效
const isEmailValid = ref(false)
//有效的条件
const onValidate = (prop, isValid) => {
    if (prop === 'email')
        isEmailValid.value = isValid
}

//表单内容是否全部有效
const formRef = ref()
//注册请求
const register = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post('/api/auth/register', {
                username: form.username,
                password: form.password,
                email: form.email,
                code: form.code
            }, (message) => {
                ElMessage.success(message)
                router.push("/")
            })
        } else {
            ElMessage.warning('请将注册表单内容填写完整！')
        }
    })
}

const rules = {
    username: [
        {validator: validateUsername, trigger: ['blur', 'change']},
        {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']},
    ],
    password: [
        {required: true, message: '请输入密码', trigger: 'blur'},
        {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
    ],
    password_repeat: [
        {validator: validatePassword, trigger: ['blur', 'change']},
    ],
    email: [
        {required: true, message: '请输入邮件地址', trigger: 'blur'},
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
    ],
    code: [
        {required: true, message: '请输入获取的验证码', trigger: 'blur'},
    ]
}

//限制获取验证码时间 1分钟
const coldTime = ref(0)
//发送获取验证码请求
const sendEmail = () => {
    coldTime.value = 60
    post('/api/auth/sendEmail', {
        email: form.email,
        hasAccount: false
    }, (message) => {
        ElMessage.success(message)
        setInterval(() => coldTime.value--, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

</script>

<template>
    <div style="text-align: center;margin: 0 20px;">
        <div style="margin-top:80px">
            <div style="font-size: 25px;font-weight: bolder">注册</div>
            <div style="font-size: 10px;color: grey">继续，即表示您正在申请一个帐户并同意我们的用户协议和隐私政策。
            </div>
        </div>

        <div style="margin-top: 30px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名">
                        <template #prefix>
                            <el-icon>
                                <User/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" :maxlength="16" type="password" placeholder="密码">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password_repeat">
                    <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="确认密码">
                        <template #prefix>
                            <el-icon>
                                <Lock/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="form.email" type="email" placeholder="电子邮件">
                        <template #prefix>
                            <el-icon>
                                <Message/>
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-row :gutter="10" style="width: 100%">
                        <el-col :span="17">
                            <el-input v-model="form.code" :maxlength="6" type="text" placeholder="电子邮件验证码">
                                <template #prefix>
                                    <el-icon>
                                        <ChatDotSquare/>
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="7">
                            <el-button @click="sendEmail" type="success"
                                       :disabled="!isEmailValid || coldTime > 0">
                                {{ coldTime > 0 ? '请等待 ' + coldTime + ' 秒' : '获取验证码' }}
                            </el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin-top: 30px">
            <el-button @click="register" style="width: 270px" type="success" plain>立即注册</el-button>
        </div>

        <el-divider>
            <span style="font-size: 10px;color: gray">已有账号</span>
        </el-divider>

        <div>
            <el-button @click="router.push('/')" style="width: 270px" type="warning" plain>前往登录</el-button>
        </div>
    </div>
</template>

<style scoped>

</style>