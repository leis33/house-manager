package nbu.dbProject.services;

import nbu.dbProject.entity.Company;
import nbu.dbProject.dao.CompanyRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CompanyService {
    private static final Logger logger = LogManager.getLogger(CompanyService.class);
    private final CompanyRepository companyRepository;

    public CompanyService() {
        this.companyRepository = new CompanyRepository();
    }

    public Company createCompany(Company company) {
        logger.info("Creating company: {}", company.getName());
        ValidationUtil.validate(company);
        return companyRepository.save(company);
    }

    public Company updateCompany(Company company) {
        logger.info("Updating company: {}", company.getId());
        ValidationUtil.validate(company);
        return companyRepository.update(company);
    }

    public void deleteCompany(Long id) {
        logger.info("Deleting company: {}", id);
        companyRepository.delete(id);
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<Company> getCompaniesSortedByRevenue() {
        return companyRepository.findAllSortedByRevenue();
    }
}
