package dev.yashgarg.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule val benchmarkRule = MacrobenchmarkRule()

    @Test fun startupNoCompilation() = startup(CompilationMode.None())

    @Test fun startupBaselineProfile() = startup(CompilationMode.Partial())

    private fun startup(mode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = packageName,
            metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD,
            compilationMode = mode,
            setupBlock = { pressHome() }
        ) {
            startActivityAndWait()

            navigateToConfigFragment()
        }

    companion object {
        const val packageName = "dev.yashgarg.qbit"
    }
}

//    StartupBenchmark_startupNoCompilation
//    timeToInitialDisplayMs   min 459.4,   median 487.8,   max 581.8
//    frameDurationCpuMs   P50   62.7,   P90   67.8,   P95   87.5,   P99  124.2
//    frameOverrunMs   P50   41.6,   P90   56.7,   P95  169.6,   P99  265.5
//    Traces: Iteration 0 1 2 3 4
//    StartupBenchmark_startupBaselineProfile
//    timeToInitialDisplayMs   min 459.0,   median 471.4,   max 516.2
//    frameDurationCpuMs   P50   48.5,   P90   65.8,   P95   67.8,   P99  116.4
//    frameOverrunMs   P50   26.1,   P90   51.0,   P95  104.3,   P99  215.0
//    Traces: Iteration 0 1 2 3 4
