package wallapp.log

import java.util.FormatFlagsConversionMismatchException
import java.util.IllegalFormatConversionException
import java.util.MissingFormatArgumentException
import java.util.MissingFormatWidthException
import java.util.UnknownFormatConversionException


class LogEmitterDesktop : LogEmitter {

    private fun validateMessage(message: String?, vararg args: Any?) {
        if (message == null) return
        if (args.isNotEmpty()) {
            message.count { it == '%' }.let { count ->
                if (count > 0) {
                    require(count == args.size) {
                        "Message contains $count placeholders, but ${args.size} arguments were provided.\n  message: $message, args: ${args.joinToString()}"
                    }
                }
            }
        }
    }

    private fun print(type: String, throwable: Throwable?, message: String?, vararg args: Any?) {
//        val formattedMessage = message?.format(*args) ?: ""
//        val formattedMessage = message?.format(*args.toList().toTypedArray())
        /**
         * Logging the ImageHash results causes Exceptions to be raised on desktop.
         * Just ignore these for now.
         */
        val formattedMessage = try {
            message?.format(locale = null, *args)
        } catch (e: UnknownFormatConversionException) {
            // "UEP==;jZ^%j[jtMyfPxu~UayIVfQj[-:j[D%"
            message
        } catch (e: MissingFormatArgumentException) {
            // "UMM~qNiHnz%hHWRjVt%gkiR%tlr=.6N{aiNG"
            message
        } catch (e: IllegalFormatConversionException) {
            // "UPSF@XkC^%adofWCaxt6~Uax9bkCRjt6ogM|"
            message
        } catch (e: MissingFormatWidthException) {
            // "UKJtq?%0x]Rj~p?axuRjS%%Ns-t6-;t7NGRk"
            message
        } catch (e: FormatFlagsConversionMismatchException) {
            // "ULKdFwm9G@+_{K#TrERk4S+]BoR,4@v#%0tQ"
            message
        }?: ""

        println("[$type]: $formattedMessage")
    }

    override fun e(message: String, vararg args: Any?) {
//        Timber.tag(tag)
//        validateMessage(message, *args)
//        Timber.e(message, *args)
        print("E", throwable = null, message, args, )
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("E", t, message, args)
    }

    override fun w(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("W", throwable = null, message, args)
    }

    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("W", t, message, args)
    }

    override fun i(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("I", throwable = null, message, args)
    }

    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("I", t, message, args)
    }

    override fun d(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("D", throwable = null, message, args)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("D", t, message, args)
    }

    override fun v(message: String, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("V", throwable = null, message, args)
    }

    override fun v(t: Throwable?, message: String?, vararg args: Any?) {
//        Timber.tag(tag)
        validateMessage(message, *args)
        print("V", t, message, args)
    }
}