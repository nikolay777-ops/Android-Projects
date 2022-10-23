package com.example.myapplication

import java.util.regex.Pattern


class TextParsers{

    companion object{
        @JvmStatic
        fun findPeriod(text: String): String{
            var periodPattern: String
            var textPart = text.slice(text.indexOf(".")+1 until text.length)
            if(textPart.length > 1 && text.contains(".")){

                periodPattern = textPart[textPart.length-1].toString()
                var length: Int = (textPart.length / 2) + 1

                while (periodPattern.length < length){
                    val start = textPart.length - (2 * periodPattern.length)
                    val end = textPart.length - periodPattern.length - 1
                    val secondPattern = textPart.slice(start..end)
                    if ((secondPattern != periodPattern) &&
                        (text.slice(text.indexOf(".")+1 until text.length).length == textPart.length)){
                        periodPattern = textPart.slice(
                            textPart.length-periodPattern.length-1 until textPart.length
                        )
                    }
                    else{
                        textPart = textPart.slice(0 until textPart.length-(periodPattern.length*2))
                        length = (textPart.length / 2) + 1
                    }

                }

                if(periodPattern.isNotEmpty()){

                    var textLeft = text.slice(0 until text.indexOf('.')+1)
                    textPart = text.slice(text.indexOf(".")+1 until text.length)
                    textPart = textPart.slice(0 until textPart.length - periodPattern.length)

                    if(Pattern.compile(periodPattern).matcher(textPart.slice(textPart.indices)).find()){
                        textPart = textPart.slice(0 until textPart.indexOf(periodPattern))
                        textLeft = textLeft.plus(textPart)
                        textLeft = textLeft.plus("($periodPattern)")
                        return textLeft
                    }

                    return text
                }
            }
            return text
        }

        @JvmStatic
        fun zeroParser(text: String) : Boolean{
            if(text.length == 1){
                return true
            }
            if(text.contains(".")){
                val leftPart = text.subSequence(0..text.indexOf("."))
                if (leftPart.startsWith("0") && leftPart.contains("[1-9]".toRegex())){
                    return false
                }
            }
            else{
                if (text.startsWith("0") && text.contains("[1-9]".toRegex())){
                    return false
                }
            }

            return true
        }

        @JvmStatic
        fun dotParser(text : String) : Boolean{
            if(text[0] == '.' && text.length == 1){
                return false
            }
            if(text[0] == '0' && text[1] == '.'){
                return false
            }
            return true
        }

        @JvmStatic
        fun zeroEndParser(text:String) : String {
            var newText: String = text
            var index = -2
            if(text.contains(".")){
                var rightPart = text.subSequence(text.indexOf(".")+1 until text.length)
                if (rightPart.endsWith("0")){
                    index = rightPart.length - 1
                    for (i in rightPart.length-1 downTo  0){
                        if(rightPart[i] == '0'){
                            index = i
                        }
                        else{
                            break
                        }
                    }
                }

                if (index != -2){
                    rightPart = rightPart.slice(0 until index)
                    if(rightPart.isNotEmpty()){
                        newText = newText.slice(0..text.indexOf("."))
                        newText = newText.plus(rightPart)
                        return newText
                    }
                    newText = newText.slice(0..text.indexOf(".")-1)
                }
                }

            return newText
            }
        }
    }
