package bgu.spl.a2;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
    private volatile int current_version;
    public VersionMonitor(){
        this.current_version = 0;
    }

    synchronized public int getVersion() { return current_version; }

    synchronized public void inc() {
        current_version++;
        notifyAll();
    }

    synchronized public void await(int version) throws InterruptedException {
        while(getVersion() == version){
            wait();
        }
    }
}
