package com.shuyu.gsygithubappcompose.feature.detail.file

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shuyu.gsygithubappcompose.core.ui.components.GSYGeneralLoadState
import com.shuyu.gsygithubappcompose.core.ui.components.GSYPullRefresh
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import com.shuyu.gsygithubappcompose.data.repository.vm.BaseScreen
import java.net.URLEncoder
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoOwner
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoName
import com.shuyu.gsygithubappcompose.feature.detail.LocalRepoDetailFileViewModel

@Composable
fun RepoDetailFileScreen(
) {
    val viewModel = LocalRepoDetailFileViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current
    val owner = LocalRepoOwner.current
    val repoName = LocalRepoName.current

    LaunchedEffect(owner, repoName) {
        viewModel.setRepoInfo(owner, repoName)
        viewModel.doInitialLoad()
    }

    BaseScreen(viewModel = viewModel) {
        GSYGeneralLoadState(
            isLoading = uiState.isPageLoading && uiState.fileContents.isEmpty(),
            error = uiState.error,
            retry = { viewModel.doInitialLoad() }) {
            Column(Modifier.fillMaxSize()) {
                PathNavigator(
                    pathSegments = uiState.pathSegments, onSegmentClick = { index ->
                        val path = uiState.pathSegments.subList(0, index + 1).joinToString("/")
                        viewModel.navigateToPath(path)
                    },
                    //onNavigateUp = { viewModel.navigateUp() },
                    isLoading = uiState.isPageLoading || uiState.isRefreshing || uiState.isLoadingMore
                )

                GSYPullRefresh(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    isLoadMore = false,
                    onLoadMore = { },
                    hasMore = false,
                    itemCount = uiState.fileContents.size,
                    loadMoreError = false
                ) {
                    items(uiState.fileContents.size) { index ->
                        val file = uiState.fileContents[index]
                        FileItem(file = file, onClick = {
                            if (file.type == "dir") {
                                viewModel.navigateToPath(file.path)
                            } else if (!isImageOrArchive(file.name)) {
                                navigator.navigate("file_code/$owner/$repoName/${URLEncoder.encode(file.path, "UTF-8")}")
                            }
                        })
                    }
                }
            }
        }
    }
}

fun isFileViewable(fileName: String): Boolean {
    val viewableExtensions = listOf(
        // --- 基础文本和数据 (Basic Text & Data) ---
        "txt", "json", "xml", "csv",     // *新增: CSV 数据*
        "tsv",     // *新增: TSV 数据*
        "log",     // 日志文件
        "md",      // Markdown
        "rst",     // *新增: reStructuredText*
        "tex",     // *新增: LaTeX*

        // --- Web 开发 (Web Development) ---
        "html", "css", "js", "ts",      // TypeScript
        "jsx",     // JavaScript XML (React)
        "tsx",     // TypeScript XML (React)
        "vue",     // Vue.js
        "svelte",  // *新增: Svelte*
        "scss",    // SASS
        "sass",    // SASS
        "less",    // LESS
        "styl",    // *新增: Stylus*
        "svg",     // *新增: SVG (可读的 XML)*

        // --- 移动端 & 跨平台 (Mobile & Cross-platform) ---
        "java", "kt",      // Kotlin
        "dart",    // Flutter / Dart
        "swift",   // Apple Swift
        "m",       // Objective-C
        "h",       // C/C++/Objective-C Header
        "metal",   // *新增: Apple Metal Shader*

        // --- 后端 & 通用语言 (Backend & General Purpose) ---
        "py",      // Python
        "go",      // Go
        "rs",      // Rust
        "cs",      // C# (C-Sharp)
        "rb",      // Ruby
        "php",     // PHP
        "c", "cpp",     // C++
        "hpp",     // C++ Header
        "lua",     // *新增: Lua*
        "perl",    // *新增: Perl*
        "pl",      // *新增: Perl*
        "scala",   // *新增: Scala*
        "r",       // *新增: R (统计)*
        "ex",      // *新增: Elixir*
        "exs",     // *新增: Elixir Script*
        "erl",     // *新增: Erlang*

        // --- 配置文件 & 构建 (Config & Build) ---
        "yml", "yaml",    // *新增: YAML (YML 的别名)*
        "gitignore", "gradle", "gradle.kts", // *新增: Gradle Kotlin DSL*
        "properties", "toml",    // TOML
        "ini",     // INI
        "conf",    // Configuration
        "dockerfile", // Docker
        "env",     // *新增: 环境变量文件*
        "rc",      // *新增: 配置文件 (如 .zshrc, .bashrc)*
        "pro",     // *新增: ProGuard 规则*
        "tf",      // *新增: Terraform*
        "hcl",     // *新增: HashiCorp Config Language (Terraform)*

        // --- 数据库 & IDL (Database & IDL) ---
        "sql",     // SQL
        "proto",   // *新增: Protocol Buffers (gRPC)*
        "graphql", // *新增: GraphQL Schema*
        "gql",     // *新增: GraphQL Schema*

        // --- 脚本 (Scripting) ---
        "sh",      // Shell Script
        "ps1",     // *新增: PowerShell*
        "bat",     // *新增: Windows Batch*
        "cmd",     // *新增: Windows Command*

        // --- 其他 (Others) ---
        "diff",    // *新增: Diff 文件*
        "patch",   // *新增: Patch 文件*
        "glsl",    // *新增: OpenGL Shading Language*
        "frag",    // *新增: Fragment Shader*
        "vert",    // *新增: Vertex Shader*
        "sln",     // *新增: Visual Studio Solution*
        "csproj",  // *新增: C# Project*
        "vbproj",  // *新增: Visual Basic Project*
        "mak",     // *新增: Makefile*
        "cmake",   // *新增: CMake*
        "license"  // *新增: LICENSE 文件*
    )
    val extension = fileName.substringAfterLast('.', "")
    return extension in viewableExtensions
}

fun isImageOrArchive(fileName: String): Boolean {
    val extensions = listOf("png", "jpg", "jpeg", "gif", "bmp", "webp", "zip", "rar", "7z", "tar", "gz")
    val extension = fileName.substringAfterLast('.', "").lowercase()
    return extension in extensions
}


@Composable
fun PathNavigator(
    pathSegments: List<String>, onSegmentClick: (Int) -> Unit, isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color.White
    ) { // Using PrimaryLight directly
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LazyRow(Modifier.padding(8.dp), state = rememberLazyListState()) {
                    item {
                        Text(
                            text = ".",
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable(enabled = !isLoading) { onSegmentClick(-1) }, // -1 to represent root
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (pathSegments.isNotEmpty()) {
                            Text(text = ">")
                        }
                    }
                    items(pathSegments.size) { index ->
                        val segment = pathSegments[index]
                        Text(
                            text = segment,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable(enabled = !isLoading) { onSegmentClick(index) },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (index < pathSegments.size - 1) {
                            Text(text = ">")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: com.shuyu.gsygithubappcompose.core.network.model.FileContent, onClick: () -> Unit
) {
    val isDirectory = file.type == "dir"
    val isClickable = isDirectory || !isImageOrArchive(file.name)
    Surface(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(enabled = isClickable) { onClick() }, tonalElevation = 2.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = if (isDirectory) "Folder" else "File",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = file.name)
        }
    }
}