# WordTranslate

**WordTranslate** 是一款简单实用的单词翻译应用程序，旨在帮助用户轻松翻译单词和短语。您可以通过下载我们的 APK 文件，快速开始使用。同时，您还可以根据需要定制自己的 API 以获得个性化的翻译体验。

## 特色功能

- **快速翻译**：支持多种语言的单词翻译。
- **定制 API**：可以使用自己的 API 进行翻译。
- **持续维护**：我们定期更新应用程序，并欢迎用户反馈和报告问题。

## 下载和安装

1. 下载 APK 文件：请访问 `WordTranslate/app/release/` 文件夹中的 `app-release.apk`。
2. 安装 APK 文件：在您的 Android 设备上打开 APK 文件进行安装。

## 有道翻译 API

1. 使用有道翻译API https://dict.youdao.com/suggest?num=1&doctype=json&q=`word`。
2. 直接将word替换为搜索单词即可，无需申请key。
- 使用示例
- https://dict.youdao.com/suggest?num=1&doctype=json&q=apple
- 得到如下结果
```json
{
    "result": {
        "msg": "success",
        "code": 200
    },
    "data": {
        "entries": [
            {
                "explain": "n. 苹果",
                "entry": "apple"
            }
        ],
        "query": "apple",
        "language": "en",
        "type": "dict"
    }
}
```

## 反馈和支持

我们欢迎用户报告发现的任何问题或建议，以帮助我们改进应用程序。您可以通过以下方式与我们联系：

- 提交 [GitHub Issues](https://github.com/yourusername/yourrepository/issues)
- 发送电子邮件至：support@wordtranslateapp.com

## 许可证

本项目采用 [MIT 许可证](LICENSE)，详细信息请参阅 LICENSE 文件。

感谢您的使用与支持！

---

*请确保遵循以上步骤进行安装和配置。如有任何问题，请随时与我们联系。*
