<template>
  <div class="example">
    <MdEdit :value="MdValue" :handle-change="onMdChange" />
    <CodeEditor :value="codeValue" :handle-change="onCodeChange" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import MdEdit from "@/components/MdEdit.vue";
import CodeEditor from "@/components/CodeEditor.vue"; // @ is an alias to /src

const codeValue = ref();
const MdValue = ref();
const onMdChange = (v: string) => {
  MdValue.value = v;
  console.log(v);
};

const onCodeChange = (v: string) => {
  codeValue.value = v;
  // console.log(v);
};
</script>
