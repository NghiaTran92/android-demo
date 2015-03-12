package com.viki.epubreader.objects;

import java.math.BigDecimal;
import java.util.List;

import com.viki.core.parser.TableOfContent.Chapter;



public class BookState {

	public long _id;
	public int curChapter = 1;
	public int curPage = 1;
	public float curPercentage;
	public String curHtml;
	public String curStyle = null;
	public int totalPagesOfChapter;
	//public int totalPages;
	public int totalChapter;
	public int realPage = 1;
	public String listChapterContent;

	public BookState() {

	}

	public BookState(long id, int chapter, int page, float percentage,
			String html, String style) {
		_id = id;
		curChapter = chapter;
		curPage = page;
		curPercentage = percentage;
		curHtml = html;
		curStyle = style;
	}

	public Boolean isEndOfBook() {
		if (curPage == totalPagesOfChapter && curChapter == totalChapter) {
			return true;
		}
		return false;
	}

	public Boolean isStartOfBook() {
		if (curPage == 1 && curChapter == 1) {
			return true;
		}
		return false;
	}

	public Boolean isEndOfChap() {
		if (curPage == totalPagesOfChapter) {
			return true;
		}
		return false;
	}

	public Boolean isStartOfChap() {
		if (curPage == 1) {
			return true;
		}
		return false;
	}

	public void setNewRealPage(int realPage, List<Chapter> listChapers) {
		this.realPage = realPage;
		int selectedChapter = 0;

		for (int i = 0, n = totalChapter; i < n; i++) {
			Chapter chapter = listChapers.get(i);
			if (chapter.getStartPage() <= realPage
					&& chapter.getEndPage() >= realPage) {
				selectedChapter = i + 1;
				break;
			}
		}
		if (selectedChapter == 0)
			return;
		curChapter = selectedChapter;
		curPage = realPage
				- listChapers.get(selectedChapter - 1).getStartPage() + 1;

		float pagePercent = curPage
				/ (float) (listChapers.get(selectedChapter - 1).getEndPage()
						- listChapers.get(selectedChapter - 1).getStartPage() + 1);

		BigDecimal bd = new BigDecimal(pagePercent);
		BigDecimal rounded = bd.setScale(6, BigDecimal.ROUND_HALF_UP);
		curPercentage = rounded.floatValue();
	}

	public void backChap() {
		if (curChapter > 0) {
			curPage = 1;
			curChapter--;
		}
	}

	public void updatePercentTage() {
		if (totalPagesOfChapter > 0) {
			float pagePercent = 100 * curPage / totalPagesOfChapter;

			curPercentage = pagePercent;
		}
	}

	public void setTotalPagesOfChapter(int totalPagesOfChapter) {
		this.curPage = Math.round(curChapter * totalPagesOfChapter
				/ this.totalPagesOfChapter);
		this.realPage = Math.round(realPage * totalPagesOfChapter
				/ this.totalPagesOfChapter);
		this.totalPagesOfChapter = totalPagesOfChapter;
	}

	public int getCurChapter() {
		return curChapter;
	}

	public void setCurChapter(int curChapter) {
		this.curChapter = curChapter;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		curPage = curPage < 1 ? 1 : curPage;
		this.curPage = curPage;
	}

	public float getCurPercentage() {
		return curPercentage;
	}

	public void setCurPercentage(float curPercentage) {
		curPercentage = curPercentage < 0 ? 0 : curPercentage;
		this.curPercentage = curPercentage;
	}

	public String getCurHtml() {
		return this.curHtml;
	}

	public void setCurHtml(String html) {
		this.curHtml = html;
	}

	@Override
	public String toString() {
		return "BookState [_id=" + _id + ", curChapter=" + curChapter
				+ ", curPage=" + curPage + ", curPercentage=" + curPercentage;
	}

	public String getCurStyle() {
		return curStyle;
	}

	public void setCurStyle(String curStyle) {
		this.curStyle = curStyle;
	}
}
