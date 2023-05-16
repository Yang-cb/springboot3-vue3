<script setup>

import {ChatDotSquare, Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const form = reactive({
    email: '',
    code: '',
    password: ''
})

//电子邮件是否有效
const isEmailValid = ref(false)
//有效的条件
const onValidate = (prop, isValid) => {
    if (prop === 'email')
        isEmailValid.value = isValid
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

const rules = {
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
        hasAccount: true
    }, (message) => {
        ElMessage.success(message)
        setInterval(() => coldTime.value--, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

//重置密码步骤1，验证邮箱
const formRef = ref()
const reset1SendEmail = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post('/api/auth/reset1SendEmail', {
                email: form.email,
                code: form.code
            }, () => {
                active.value++
            })
        } else {
            ElMessage.warning('请填写电子邮件地址或验证码')
        }
    })
}
//重置密码步骤2，重置密码
const reset2Password = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post('/api/auth/reset2Password', {
                email: form.email,
                password: form.password
            }, (message) => {
                ElMessage.success(message)
                router.push('/')
            })
        } else {
            ElMessage.warning('请填写新的密码')
        }
    })
}
//步骤
const active = ref(0)
</script>

<template>
    <div style="margin: 30px 20px">
        <el-steps :active="active" finish-status="success" align-center>
            <el-step title="验证"/>
            <el-step title="重置"/>
        </el-steps>
    </div>
  <!--步骤1-->
    <transition name="el-fade-in-linear" mode="out-in">
        <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 0">
            <div style="margin-top:60px">
                <div style="font-size: 25px;font-weight: bolder">重置密码</div>
                <div style="font-size: 10px;color: grey">告诉我们与您的帐户关联的电子邮件地址，我们将向您发送一封电子邮件，其中包含重置密码所需的验证码。
                </div>
            </div>
            <div style="margin-top: 30px">
                <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
                <el-button @click="reset1SendEmail" style="width: 270px" type="success" plain>前往重置</el-button>
            </div>

            <el-row style="margin-top: 40px">
                <el-col>
                    <el-link @click="router.push('/')">登录</el-link>
                    <span>&nbsp;&bullet;&nbsp;</span>
                    <el-link @click="router.push('/register')">注册</el-link>
                </el-col>
            </el-row>
        </div>
    </transition>
  <!--步骤2-->
    <transition name="el-fade-in-linear" mode="out-in">
        <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 1">
            <div style="margin-top:60px">
                <div style="font-size: 25px;font-weight: bolder">重置密码</div>
            </div>
            <div style="margin-top: 30px">
                <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                    <el-form-item prop="password">
                        <el-input v-model="form.password" :maxlength="16" type="password" placeholder="新密码">
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
                </el-form>
            </div>

            <div style="margin-top: 30px">
                <el-button @click="reset2Password" style="width: 270px" type="success" plain>立即重置</el-button>
            </div>
        </div>
    </transition>
</template>

<style scoped>

</style>