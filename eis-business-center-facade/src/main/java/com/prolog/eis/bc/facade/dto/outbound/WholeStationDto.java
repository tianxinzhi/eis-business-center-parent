package com.prolog.eis.bc.facade.dto.outbound;

/**
 * @Describe
 * @Author clarence_she
 * @Date 2021/10/22
 **/
public class WholeStationDto {

    private String stationId;
    /**
     * 是否锁定 0-unlock
     */
    private int isLock;

    /**
     * 是否索取
     */
    private int isClaim;

    /**
     * 在途料箱最大缓存数
     */
    private int maxLxCacheCount;
    /**
     * 到达的容器数量
     */
    private int arriveLxCount;
    /**
     * 料箱出库绑定的数量
     */
    private int chuKuLxCount;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public int getIsLock() {
        return isLock;
    }

    public void setIsLock(int isLock) {
        this.isLock = isLock;
    }

    public int getIsClaim() {
        return isClaim;
    }

    public void setIsClaim(int isClaim) {
        this.isClaim = isClaim;
    }

    public int getMaxLxCacheCount() {
        return maxLxCacheCount;
    }

    public void setMaxLxCacheCount(int maxLxCacheCount) {
        this.maxLxCacheCount = maxLxCacheCount;
    }

    public int getArriveLxCount() {
        return arriveLxCount;
    }

    public void setArriveLxCount(int arriveLxCount) {
        this.arriveLxCount = arriveLxCount;
    }

    public int getChuKuLxCount() {
        return chuKuLxCount;
    }

    public void setChuKuLxCount(int chuKuLxCount) {
        this.chuKuLxCount = chuKuLxCount;
    }
}
