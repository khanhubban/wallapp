Instructions for Strings

# High level rules

1. Every string must have an English(US) translation in the `strings_en.json` file.
2. Every string must have a property in `wallapp.resources.string.Strings`.

# To add a new langauge

1. Create a new file in `.shared/data/resources/src/commonMain/composeResources/files/` named `strings_XX.json ` where `xx` is the language code.
2. Add a file definition in `LocalFileAssetDefault`.

## Prompt for translating strings

All translations should be handled via ChatGPT. The following is a prompt that can be used as a template.
Feel free to modify this prompt if you need to add more context or instructions.

"""
I have a `strings_en.json` file in my app that is used for the translations in our Android/iOS app 
called WallApp.

Here is an example of the file:
```
{
  "about": "About",
  "aboutPlus": "About Plus",
  "abstract": "Abstract",
  "appName": "WallApp",
  "appStore": "App Store",
  "appUpdateRequired": "App Update Required",
  "downloadingProgressCountTemplate": "%d of %d",
  "downloadingProgressCountMessage": "Downloading %d of %d",
  "downloadingWallpaperTitle": "Downloading %s to %s album"
}
```

Note that each string has a unique key and a corresponding value.

The translation system loads these strings at runtime based on the device’s system language(s).

We require some strings, pasted at the end of this prompt, to be translated from English (US) to [INSERT_LANGUAGE_HERE].

Most important points when translating:

	1.	The JSON key MUST be the same as appears in the English string.
	2.	Take special care of strings that include formatting, such as "downloadingProgressCountTemplate": "%d of %d". THE TRANSLATED STRING MUST INCLUDE THE FORMATTING TOKENS IN THE SAME FORMAT AS THE SOURCE STRING.
	3.	The app runs on phones, so screen space is always at a premium. When translating, favor shorter strings/translations.
	4.	The following words/phrases should be kept in their English spelling in all languages: "Google", "Apple".
	5.	Ensure proper grammar and spelling for the target language.
	6.	Maintain consistency in terminology, especially for UI elements.

Please provide the translated JSON strings for the following strings:

```
<!-- 
NOTE that ChatGPT will not be able to translate all strings at once - typically ChatGPT will 
reliably translate ~100 strings at a time, but can be unreliable after that number. 
You will have to do the translations in batches.
-->
[INSERT STRINGS TO TRANSLATE HERE]
```

Do not explain your reasoning for the translations, as we are only interested in the translated 
strings themselves. Return JSON as a response that can be copied straight into a `strings_xx.json` file.

"""
