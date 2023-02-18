package com.zzj.xiaomiwidgettest.rxjava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.observables.GroupedObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/16
 * @since 1.0.0
 */
public class RxJavaTest {

    public static RxJavaTest INSTANCE = new RxJavaTest();

    public void test1() {
        //1.创建被观察者
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("hello world");
                emitter.onComplete();
            }
        });
        //2.创建观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(@NonNull String s) {
                System.out.println("onNext():" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("onError():" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        };
        //3.订阅事件
        observable.subscribe(observer);

    }

    public void testJust() {
        Observable.just("hello world").subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

    }

    public void testFrom() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("Hello" + i);
        }

        Observable.fromArray(list).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(List<String> strings) {
                System.out.println(strings);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError():" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        });

    }

    String value;
    public void testDefer() {
        value = "2020/12/13";
        Observable<String> observable = Observable.defer(new Supplier<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> get() throws Throwable {
                return Observable.just(value);
            }
        });
        value = "12345";
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        });

    }

    public void testError() {
        Observable.defer(new Supplier<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> get() throws Throwable {
                return Observable.error(new Throwable("你写了个bug"));
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(@NonNull Object o) {
                System.out.println("onNext():" + o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        });

    }

    public void testInterval() {
        //TrampolineScheduler不会立即执行，当其他排队任务结束时才执行，TrampolineScheduler运行在主线程。
        Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.trampoline()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                System.out.println("onNext():" + aLong);
                if (aLong > 20){
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        });

    }


    public void testRange() {
        //TrampolineScheduler不会立即执行，当其他排队任务结束时才执行，TrampolineScheduler运行在主线程。
        Observable.range(1, 20).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(@NonNull Integer aLong) {
                System.out.println("onNext():" + aLong);
                if (aLong > 20){
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete():");
            }
        });

    }

    public void testMap() {
        //Integer to String
        Observable.just(123).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer s) throws Exception {
                return String.valueOf(s*100);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void testFlatMap() {
        Observable.just(1, 2, 3, 4, 5).flatMap(new Function<Integer, ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> apply(Integer integer) throws Exception {
                return Observable.just(integer.toString());
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(String o) {
                System.out.println(o);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void testGroupBy(){
        Observable.just(1, 2, 3, 4, 5).groupBy(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer % 2==0?"偶数":"奇数";
            }
        }).subscribe(new Observer<GroupedObservable<String, Integer>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull final GroupedObservable<String, Integer> arg0) {
                arg0.subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(arg0.getKey() + "-------" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void testBuffer(){
        Observable.just(8, 1, 2, 3, 4, 5,6).buffer(3).subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Integer> integers) {
                System.out.println(integers);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void testScan() {
        Observable.range(1, 5).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(integer);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    public void testWindow() {
        //        window第一个参数count:每个窗口应发射前的最大大小;第二个:在启动新窗口之前需要跳过多少项
        Observable.range(1, 5).window(5, 1).subscribe(new Observer<Observable<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe():");
            }

            @Override
            public void onNext(final Observable<Integer> arg0) {
                System.out.println(arg0);
                arg0.subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("onNext onSubscribe():");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("---"+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }


}
