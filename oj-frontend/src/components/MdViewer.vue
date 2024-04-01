<template>
  <Viewer :value="value" :plugins="plugins" />
</template>
<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import { Viewer } from "@bytemd/vue-next";
import { withDefaults, defineProps } from "vue";

/**
 * 定义组件属性
 */
interface Props {
  value: string;
}

const plugins = [
  gfm(),
  highlight(),
  // Add more plugins here
];

/**
 * 给组件定义初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
});
</script>

<style>
.bytemd-tippy-right:last-child {
  display: none;
}
</style>
