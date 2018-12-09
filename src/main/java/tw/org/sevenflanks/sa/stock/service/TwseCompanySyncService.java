package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.picker.TwseCompanyPicker;

@Service
@Transactional
public class TwseCompanySyncService implements GenericSyncService<TwseCompany, TwseCompanyDao> {

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private TwseCompanyPicker twseCompanyPicker;

	@Override
	public TwseCompanyDao dao() {
		return twseCompanyDao;
	}

	@Override
	public Class<TwseCompany> entityClass() {
		return TwseCompany.class;
	}

	@Override
	public List<TwseCompany> fetch(LocalDate date) {
		// 公司別的API沒有日期輸入參數
		return Optional.ofNullable(twseCompanyPicker.getAll())
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(TwseCompany::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_company";
	}

	@Override
	public String zhName() {
		return "上市公司資訊";
	}
}
