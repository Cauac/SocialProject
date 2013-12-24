package angularspringapp.loader;

import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class LoaderThreadRunner implements InitializingBean {

    List<Thread> loadersThreadList;

    public void setLoadersThreadList(List<Thread> loadersThreadList) {
        this.loadersThreadList = loadersThreadList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Thread thread : loadersThreadList) {
            thread.start();
        }
    }
}
